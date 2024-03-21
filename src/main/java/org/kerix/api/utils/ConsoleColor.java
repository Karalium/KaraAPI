package org.kerix.api.utils;


public enum ConsoleColor {
    AQUA("\u001b[1;36m"),
    BLACK("\u001b[0;30m"),
    BLUE("\u001b[1;34m"),
    BOLD("\u001b[1m"),
    DARK_AQUA("\u001b[0;36m"),
    DARK_BLUE("\u001b[0;34m"),
    DARK_GRAY("\u001b[1;30m"),
    DARK_GREEN("\u001b[0;32m"),
    DARK_PURPLE("\u001b[0;35m"),
    DARK_RED("\u001b[0;31m"),
    GOLD("\u001b[1;33m"),
    GRAY("\u001b[0;37m"),
    GREEN("\u001b[1;32m"),
    LIGHT_PURPLE("\u001b[1;35m"),
    RED("\u001b[1;31m"),
    RESET("\u001b[0m"),
    STRIKETHROUGH("\u001b[9m"),
    STRIKETHROUGH_OFF("\u001b[29m"),
    UNDERLINE("\u001b[4m"),
    WHITE("\u001b[1;37m"),
    YELLOW("\u001b[1;33m"),
    MAGENTA("\u001b[1;35m"),
    CYAN("\u001b[1;36m"),
    ORANGE("\u001b[1;31m"),
    LIGHT_GREEN("\u001b[2;32m"),
    DARK_YELLOW("\u001b[0;33m"),
    DARK_CYAN("\u001b[0;36m"),
    LIGHT_YELLOW("\u001b[2;33m");

    private final String color;

    ConsoleColor(String ANSI) {
        this.color = ANSI;
    }

    @Override
    public String toString() {
        return color;
    }
}

