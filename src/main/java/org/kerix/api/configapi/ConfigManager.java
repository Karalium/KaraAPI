package org.kerix.api.configapi;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.kerix.api.startapi.DebugMessageAPI;
import org.kerix.api.utils.ConvertList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public abstract class ConfigManager {

    private static final HashMap<String, ConfigManager> ConfigList = new HashMap<>();
    private static boolean initFiles = false;


    public ConfigManager(@NotNull JavaPlugin plugin, @NotNull String pathFile, @NotNull String nameFile, @NotNull String key) {
        this.plugin = plugin;
        if (!pathFile.isBlank()) {
            if (!pathFile.endsWith("/")) pathFile += "/";
            if (!pathFile.startsWith("../")) pathFile = "../" + pathFile;
            pathFile = pathFile.replace(" ", "_");
        }
        if (!nameFile.endsWith(".yml")) nameFile += ".yml";
        if (!key.endsWith(".")) key += ".";

        this.nameFile = pathFile + nameFile;
        this.key = key;

        createFile();

        if (!initFiles) {
            initFiles(plugin);
        }
    }

    private final JavaPlugin plugin;
    private final String nameFile;
    private final String key;
    private File file;
    private YamlConfiguration yml;
    public abstract void initBaseConfig(YamlConfiguration yml);

    private static void initFiles(JavaPlugin plugin) {
        initFiles = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                ConfigList.values().forEach(cm -> {
                    if (!cm.getFileExists()) {
                        cm.initBaseConfig(cm.getYml());
                        cm.saveConfig();
                    }
                });
            }
        }.runTaskTimer(plugin, 1, 20L);
    }


    private void createFile() {
        file = new File(plugin.getDataFolder(), nameFile);
        if (ConfigList.get(nameFile) != null && ConfigList.get(nameFile).getFile().exists()) {
            yml = YamlConfiguration.loadConfiguration(ConfigList.get(nameFile).getFile());
        } else {
            if (!getFileExists()) {
                ConfigList.put(nameFile, this);

                yml = new YamlConfiguration();
                yml.set(getKey(), "");
                initBaseConfig(yml);
                saveConfig();
                DebugMessageAPI.configFiles(file, plugin.getLogger(), plugin);
            } else {
                yml = YamlConfiguration.loadConfiguration(file);
            }
        }
    }

    public YamlConfiguration getYml() {
        return yml;
    }

    public String getNameFile() {
        return nameFile;
    }

    public File getFile() {
        return file;
    }

    public String getKey() {
        return key;
    }

    @SuppressWarnings("all")
    public boolean getFileExists() {
        return getFile().exists();
    }

    private ConfigurationSection getYamlSection() {
        loadConfig();
        return yml.getConfigurationSection(getKey());
    }

    public Integer getInt(@NotNull String index) {
        return getYamlSection().getInt(index);
    }

    public double getDouble(@NotNull String index) {
        return getYamlSection().getDouble(index);
    }

    public String getString(@NotNull String index) {
        return getYamlSection().getString(index);
    }

    public List<?> getList(@Nonnull String index) {
        List<?> rawList = getYamlSection().getList(index);
        return (rawList != null) ? ConvertList.type(getYamlSection().getList(index), getType(rawList)) : Collections.emptyList();
    }

    public @NotNull List<String> getConfigurationSection(@NotNull String index, boolean deep) {
        ConfigurationSection sectionList = this.getYamlSection().getConfigurationSection(index);
        return (sectionList != null) ? new ArrayList<>(sectionList.getKeys(deep)) : Collections.emptyList();
    }
    @Deprecated
    public void incrementInteger(@Nonnull String index, int value) {
        int sectionValue = getInt(index);
        sectionValue += value;

        set(index, sectionValue);
    }

    public void incrementDouble(@Nonnull String index, double value) {
        double sectionValue = getDouble(index);

        sectionValue += value;

        set(index, sectionValue);
    }

    public void set(@Nonnull String index, @Nullable Object... objects) {
        if(objects == null){
            throw new NullPointerException("Cannot set to null");
        }
        if (objects.length == 1) {
            Object object = objects[0];
            if (object instanceof List<?> list) {
                Class<?> type = getType(list);

                List<?> convertedList = ConvertList.type(list, type);
                getYamlSection().set(index, convertedList);
            } else {
                getYamlSection().set(index, object);
            }
            saveConfig();
        } else {
            List<?> list = ConvertList.array(objects);

            Class<?> type = getType(list);

            List<?> convertedList = ConvertList.type(list, type);
            getYamlSection().set(index, convertedList);
        }
    }

    public void addValueList(String index, Object... objects) {
        List<?> valuelist = ConvertList.array(getList(index) , objects);

        set(index , valuelist);
    }

    public void removeValueList(String index, Object... objects) {
        List<Object> convertList = ConvertList.array(objects);
        List<?> originalList = getList(index);

        List<?> valuelist = (originalList != null) ? originalList.stream()
                .filter(object -> !convertList.contains(object))
                .toList() : null;

        set(index , valuelist);
    }


    public void remove(@Nonnull String index) {
        loadConfig();

        getYamlSection().set(index, null);
        saveConfig();
    }

    public void saveConfig() {
        try {
            yml.save(file);
            loadConfig();
        } catch (IOException e) {
            throw new RuntimeException("Error saving config file", e);
        }
    }

    public void loadConfig() {
        try {

            yml.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Error loading config file", e);
        }
    }

    private static <T> Class<?> getType(List<T> list) {
        if (list.isEmpty()) {
            return Object.class;
        } else {
            Class<?> elementType = null;
            for (T item : list) {
                if (item != null) {
                    Class<?> itemClass = item.getClass();
                    if (elementType == null) {
                        elementType = itemClass;
                    } else if (!elementType.equals(itemClass)) {
                        return Object.class;
                    }
                }
            }
            return elementType != null ? elementType : Object.class;
        }
    }
}