package org.kerix.api.configapi;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.kerix.api.MinecraftAPI;
import org.kerix.api.startapi.DebugMessageAPI;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.kerix.api.MinecraftAPI.getINSTANCE;

public abstract class ConfigManager {

    private final DebugMessageAPI debugMessageAPI;
    private final JavaPlugin plugin;
    public abstract void initBaseConfig();
    public ConfigManager(@NotNull JavaPlugin plugin , @NotNull String pathFile , @NotNull String nameFile , @NotNull String key) {
        this.plugin = plugin;
        MinecraftAPI minecraftAPI = getINSTANCE();
        this.debugMessageAPI = minecraftAPI.getDebugMessageAPI();
        if(!pathFile.isBlank()){
            if (!pathFile.endsWith("/")) pathFile += "/";
            if(!pathFile.startsWith("../")) pathFile = "../" + pathFile;
        }
        if (!nameFile.endsWith(".yml")) nameFile += ".yml";
        if(!key.endsWith(".")) key += ".";

        this.pathFile = pathFile;
        this.nameFile = this.pathFile + nameFile;
        this.file = new File(plugin.getDataFolder() , this.nameFile);
        this.key = key;

        createFile();

        if (!initFiles) {
            initFiles(plugin);
        }
    }

    private final String pathFile;
    private static boolean initFiles = false;
    private final String nameFile;
    private File file;
    private final String key;
    private YamlConfiguration yml;
    private static final HashMap<String , ConfigManager> list = new HashMap<>();

    public static void initFiles(JavaPlugin plugin) {
        initFiles = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                list.values().forEach(cm ->{
                    if(!cm.getFileExists()) {
                        cm.initBaseConfig();
                        cm.saveConfig();
                    }
                });
            }
        }.runTaskTimer(plugin, 1 , 20L);
    }

    public void createFile() {
        file = new File(plugin.getDataFolder(), nameFile);
        if (list.get(nameFile) != null && list.get(nameFile).getFile().exists()) {
            yml = YamlConfiguration.loadConfiguration(list.get(nameFile).getFile());
        } else {
            list.put(nameFile, this);
            if(!file.exists()) {
                yml = new YamlConfiguration();
                initBaseConfig();
            } else yml = YamlConfiguration.loadConfiguration(list.get(nameFile).getFile());
            saveConfig();
            debugMessageAPI.ConfigFiles(file , plugin.getLogger() , plugin);
        }
    }

    public YamlConfiguration getYml(){
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

    public boolean getFileExists() {
        return getFile().exists();
    }

    private ConfigurationSection getYamlSection() {
        String key = getKey();
        ConfigurationSection section = yml.getConfigurationSection(key);
        if(!(section == null)) {
            return section;
        } else {
            return yml.createSection(key.replace("." , ""));
        }
    }

    public ConfigurationSection getYamlSection(@NotNull String path) {
        ConfigurationSection section = getYamlSection().getConfigurationSection(path);
        if (section != null) {
            return section;
        } else {
            throw new NullPointerException("The Yaml section does not exist: " + path);
        }
    }

    public Integer getYamlSectionInt(@NotNull String index) {
        return Objects.requireNonNull(yml.getConfigurationSection(getKey())).getInt(index);
    }

    public double getYamlSectionDouble(@NotNull String index) {
        return Objects.requireNonNull(yml.getConfigurationSection(getKey())).getDouble(index);
    }

    public String getYamlSectionString(@NotNull String index) {
        return Objects.requireNonNull(yml.getConfigurationSection(getKey())).getString(index);
    }

    public List<?> getYamlSectionList(@NotNull String index) {
        return Objects.requireNonNull(yml.getConfigurationSection(getKey())).getList(index);
    }

    public @NotNull Set<String> getYamlConfigurationSectionList(@NotNull String index) {
        ConfigurationSection sectionList = this.getYamlSection().getConfigurationSection(index);
        return (sectionList != null) ? sectionList.getKeys(false) : Collections.emptySet();
    }

    public void setYamlConfig(@NotNull String index, int value) {
        int sectionValue ;
        if (getYamlSectionInt(index) != null) {
            sectionValue = getYamlSectionInt(index);
            sectionValue += value;
        } else return;
        yml.set(getKey() + index, sectionValue);
        saveConfig();
    }

    public void setYamlConfig(@NotNull String index, double value) {
        double sectionValue;
        if (getYamlSection().get(index) instanceof Double) {
            sectionValue = getYamlSectionDouble(index);
            sectionValue += value;
        } else {
            return;
        }
        yml.set(getKey() + index, sectionValue);
        saveConfig();
    }
    public void setYamlConfig(@NotNull String index, String value) {
        List<String> convertList = new ArrayList<>();
        if (value != null) {
            if (value.contains(",")) {
                String[] splitValuesArray = value.split(",");
                convertList.addAll(Arrays.asList(splitValuesArray));
                yml.set(getKey() + index, convertList);
            } else yml.set(getKey() + index , value);
        } else {
            yml.set(getKey() + index, null);
            saveConfig();
            return;
        }

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
}