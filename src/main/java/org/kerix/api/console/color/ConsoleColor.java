package org.kerix.api.console.color;

public class ConsoleColor {
    public static final String AQUA = (new ConsoleColor("\u001b[1;36m")).getColor();
    public static String BLACK = (new ConsoleColor("\u001b[0;30m")).getColor();
    public static String BLUE = (new ConsoleColor("\u001b[1;34m")).getColor();
    public static String BOLD = (new ConsoleColor("\u001b[1m")).getColor();
    public static String DARK_AQUA = (new ConsoleColor("\u001b[0;36m")).getColor();
    public static String DARK_BLUE = (new ConsoleColor("\u001b[0;30m")).getColor();
    public static String DARK_GRAY = (new ConsoleColor("\u001b[1;30m")).getColor();
    public static String DARK_GREEN = (new ConsoleColor("\u001b[0;30m")).getColor();
    public static String DARK_PURPLE = (new ConsoleColor("\u001b[0;35m")).getColor();
    public static String DARK_RED = (new ConsoleColor("\u001b[0;31m")).getColor();
    public static String GOLD = (new ConsoleColor("\u001b[1;33m")).getColor();
    public static String GRAY = (new ConsoleColor("\u001b[0;37m")).getColor();
    public static String GREEN = (new ConsoleColor("\u001b[1;32m")).getColor();
    public static String LIGHT_PURPLE = (new ConsoleColor("\u001b[1;35m")).getColor();
    public static String RED = (new ConsoleColor("\u001b[1;31m")).getColor();
    public static String RESET = (new ConsoleColor("\u001b[0m")).getColor();
    public static String STRIKETHROUGH = (new ConsoleColor("\u001b[9m")).getColor();
    public static String STRIKETHROUGH_OFF = (new ConsoleColor("\u001b[29m")).getColor();
    public static String UNDERLINE = (new ConsoleColor("\u001b[4m")).getColor();
    public static String WHITE = (new ConsoleColor("\u001b[1;37m")).getColor();
    public static String YELLOW = (new ConsoleColor("\u001b[1;33m")).getColor();
    private final String color;

    private ConsoleColor(String ANSI) {
        this.color = ANSI;
    }

    public String getColor() {
        return this.color;
    }
}
