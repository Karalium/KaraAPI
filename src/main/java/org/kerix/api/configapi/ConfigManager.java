package org.kerix.api.configapi;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.kerix.api.MinecraftAPI;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class ConfigManager {
    private final String nameFile;
    private File file;
    private final String key;
    private YamlConfiguration yml;
    private static final HashMap<String , ConfigManager> list = new HashMap<>();
    private final MinecraftAPI minecraftAPI;
    public ConfigManager(JavaPlugin plugin , String nameFile , String key) {
        this.minecraftAPI = JavaPlugin.getPlugin(MinecraftAPI.class);
        this.nameFile = nameFile + ".yml";
        this.file = new File(plugin.getDataFolder(), this.nameFile);
        this.key = key;
        if (file.exists()) {
            this.yml = YamlConfiguration.loadConfiguration(file);
        }
    }

    public static void initFiles(JavaPlugin plugin) {
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

    public YamlConfiguration createFile(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), nameFile);
        if (list.get(nameFile) != null && list.get(nameFile).getFile().exists()) {
            yml = YamlConfiguration.loadConfiguration(list.get(nameFile).getFile());
        } else {
            yml = new YamlConfiguration();
            list.put(nameFile, this);
            minecraftAPI.getDebugMessageAPI().ConfigFiles(file , plugin.getLogger() , plugin);
            return yml;
        }
        return yml;
    }

    public YamlConfiguration getYml(){
        return yml;
    }

    public void initBaseConfig() {
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

    public ConfigurationSection getYamlSection() {
        return YamlConfiguration.loadConfiguration(file).getConfigurationSection(getKey());
    }

    public Integer getYamlSectionInt(String index) {
        return Objects.requireNonNull(YamlConfiguration.loadConfiguration(file).getConfigurationSection(getKey())).getInt(index);
    }

    public double getYamlSectionDouble(String index) {
        return Objects.requireNonNull(YamlConfiguration.loadConfiguration(file).getConfigurationSection(getKey())).getDouble(index);
    }

    public String getYamlSectionString(String index) {
        return Objects.requireNonNull(YamlConfiguration.loadConfiguration(file).getConfigurationSection(getKey())).getString(index);
    }

    public List getYamlSectionList(String index) {
        return Objects.requireNonNull(YamlConfiguration.loadConfiguration(file).getConfigurationSection(getKey())).getList(index);
    }

    public Set<String> getYamlConfigurationSectionList(String index) {
        ConfigurationSection sectionList = this.getYamlSection().getConfigurationSection(index);
        return (sectionList != null) ? sectionList.getKeys(false) : Collections.emptySet();
    }

    public void setYamlConfig(String index, int value) {
        int sectionValue ;
        if (getYamlSectionInt(index) != null) {
            sectionValue = getYamlSectionInt(index);
            sectionValue += value;
        } else return;
        yml.set(getKey() + index, sectionValue);
        saveConfig();
    }

    public void setYamlConfig(String index, double value) {
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

    public void setYamlConfig(String index, String value) {
        List<String> convertList = new ArrayList<>();
        if (value != null) {
            convertList.add(value);
            if (value.contains(",")) {
                String[] splitValuesArray = value.split(",");
                convertList.clear();
                convertList.addAll(Arrays.asList(splitValuesArray));
            }
        } else {
            yml.set(getKey() + index, null);
            saveConfig();
            return;
        }
        yml.set(getKey() + index, convertList);
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