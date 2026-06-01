package org.kerix.karaapi.api.command;

import java.util.Arrays;
import java.util.Locale;

public final class ArgumentReader {

    private final String[] args;

    public ArgumentReader(String[] args) {
        this.args = args == null ? new String[0] : args;
    }

    public int size() {
        return args.length;
    }

    public boolean empty() {
        return args.length == 0;
    }

    public boolean has(int index) {
        return index >= 0 && index < args.length;
    }

    public String get(int index) {
        if (!has(index)) {
            throw new IllegalArgumentException("Missing argument at index " + index + ".");
        }

        return args[index];
    }

    public String getOr(int index, String fallback) {
        return has(index) ? args[index] : fallback;
    }

    public String lower(int index) {
        return get(index).toLowerCase(Locale.ROOT);
    }

    public int integer(int index) {
        try {
            return Integer.parseInt(get(index));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Argument " + (index + 1) + " must be an integer.");
        }
    }

    public int integerOr(int index, int fallback) {
        if (!has(index)) {
            return fallback;
        }

        return integer(index);
    }

    public long longNumber(int index) {
        try {
            return Long.parseLong(get(index));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Argument " + (index + 1) + " must be a long number.");
        }
    }

    public double decimal(int index) {
        try {
            return Double.parseDouble(get(index));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Argument " + (index + 1) + " must be a decimal number.");
        }
    }

    public boolean bool(int index) {
        String value = lower(index);

        return switch (value) {
            case "true", "yes", "y", "1", "on" -> true;
            case "false", "no", "n", "0", "off" -> false;
            default -> throw new IllegalArgumentException(
                    "Argument " + (index + 1) + " must be true or false."
            );
        };
    }

    public String join(int startIndex) {
        if (startIndex >= args.length) {
            return "";
        }

        return String.join(" ", Arrays.copyOfRange(args, startIndex, args.length));
    }

    public String[] raw() {
        return Arrays.copyOf(args, args.length);
    }
}
