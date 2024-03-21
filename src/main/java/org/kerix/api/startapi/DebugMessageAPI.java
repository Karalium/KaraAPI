package org.kerix.api.startapi;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import javax.annotation.Nullable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.kerix.api.utils.ConsoleColor;

public class DebugMessageAPI {

    private int numberFiles = 0;

    private HashMap<File, Boolean> errorFiles = new HashMap<>();


    public void StatutPlugin(StatutMessageList args, @NotNull PluginDescriptionFile description, @NotNull Logger logger) {
        if(args != null) {
            String statut;
            ConsoleColor statutColor = switch (args) {
                case ENABLE -> {
                    statut = " On";
                    yield ConsoleColor.GREEN;
                }
                case ERROR -> {
                    statut = " Erreur";
                    yield ConsoleColor.GOLD;
                }
                case DISABLE -> {
                    statut = " Off";
                    yield ConsoleColor.RED;
                }
            };
            logStatus(logger, description.getName(), description.getVersion(), String.join("-", description.getAuthors()), statut, statutColor);
        }
    }

    public void ConfigFiles(File file, Logger logger , @Nullable Plugin plugin) {
        PluginDescriptionFile description = null;
        if(plugin == null) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length >= 3) {
                String callingClassName = stackTrace[2].getClassName();
                try {
                    Class<?> callingClass = Class.forName(callingClassName);
                    plugin = getPlugin(callingClass);
                    if (plugin != null)
                        description = plugin.getDescription();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else description = plugin.getDescription();


        if (description != null && logger != null) {
            String pluginName = description.getName().replace("_", " ");
            String author = String.join("-", description.getAuthors());

            logFileStatus(logger, pluginName, author, file);
        }
    }

    private void logStatus(Logger logger, String pluginName, String version, String author, String status, ConsoleColor statusColor) {
        int lengthPluginName = pluginName.length() + status.length();
        int lengthVersion = version.length() + 9;
        int lengthAuthors = author.length() + 3;
        int maxLength = Math.max(lengthPluginName, Math.max(lengthVersion, lengthAuthors)) + 9;
        String tiret = "-".repeat(maxLength);
        int repeatL1 = (maxLength - lengthPluginName) / 2;
        int repeatL2 = (maxLength - lengthVersion) / 2;
        int repeatL3 = (maxLength - lengthAuthors) / 2;
        StringBuilder spaceL1 = new StringBuilder(" ".repeat(repeatL1));
        StringBuilder spaceL2 = new StringBuilder(" ".repeat(repeatL2));
        StringBuilder spaceL3 = new StringBuilder(" ".repeat(repeatL3));
        String spaceBisL1 = (maxLength - lengthPluginName) % 2 != 0 ? " " : "";
        String spaceBisL2 = (maxLength - lengthVersion) % 2 != 0 ? " " : "";
        String spaceBisL3 = (maxLength - lengthAuthors) % 2 != 0 ? " " : "";
        logger.info(" ");
        logger.info(ConsoleColor.LIGHT_PURPLE + "---+----+" + tiret + "+----+---" + ConsoleColor.RESET);
        logger.info(ConsoleColor.LIGHT_PURPLE + "   |     " + spaceL1 + pluginName + statusColor + status + ConsoleColor.LIGHT_PURPLE + spaceL1 + spaceBisL1 + "     |    " + ConsoleColor.RESET);
        logger.info(ConsoleColor.LIGHT_PURPLE + "   |     " + spaceL2 + "Version: " + ConsoleColor.AQUA + version + ConsoleColor.LIGHT_PURPLE + spaceL2 + spaceBisL2 + "     |    " + ConsoleColor.RESET);
        logger.info(ConsoleColor.LIGHT_PURPLE + "   |     " + spaceL3 + "By " + ConsoleColor.AQUA + author + ConsoleColor.LIGHT_PURPLE + spaceL3 + spaceBisL3 + "     |    " + ConsoleColor.RESET);
        logger.info(ConsoleColor.LIGHT_PURPLE + "---+----+" + tiret + "+----+---" + ConsoleColor.RESET);logger.info(" ");
    }



    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private void logFileStatus(Logger logger, String pluginName, String author, File file) {
        int lengthPluginName = pluginName.length() - 4;
        String tiret = "-".repeat(lengthPluginName);

        String nameFile = file.getName();
        String pathFile = file.getAbsolutePath();

        numberFiles++;

        if (file.exists()) {
            String timestamp = dateFormat.format(new Date());
            logger.info(ConsoleColor.LIGHT_PURPLE +
                    "File " + numberFiles + ": " + ConsoleColor.AQUA + nameFile +
                    ConsoleColor.LIGHT_PURPLE + " Initialized" + ConsoleColor.RESET + "\n" +
                    "[" + timestamp + " INFO]: " + "[-+" + tiret + "+-] " + ConsoleColor.LIGHT_PURPLE +
                    "File Path: " + ConsoleColor.AQUA + pathFile + ConsoleColor.RESET);
            errorFiles.put(file, true);
        } else {
            String timestamp = dateFormat.format(new Date());
            logger.severe("Error occurred during the creation of the file " +
                    nameFile + ", please contact " + (author.contains("-") ? "the developers" : "the developer") + " of the plugin " + author + "\n" +
                    "[" + timestamp + " ERROR]: " + ConsoleColor.DARK_RED + "[-+" + tiret + "+-] " + ConsoleColor.RED + "ERROR: " +
                    ConsoleColor.YELLOW + "CHARG-FILE_" + (numberFiles <= 9 ? "0" + numberFiles : numberFiles) +
                    ConsoleColor.RESET);
            errorFiles.put(file, false);
        }
    }

    public int getNumberFiles() {
        return numberFiles;
    }

    public HashMap<File, Boolean> getErrorFiles() {
        return errorFiles;
    }

    private Plugin getPlugin(Class<?> pluginClass) {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            if (plugin.getClass().getClassLoader().equals(pluginClass.getClassLoader())) {
                return plugin;
            }
        }
        return null;
    }
}

