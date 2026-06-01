package org.kerix.karaapi.api.color;

import net.kyori.adventure.text.format.TextColor;

import java.util.Locale;
import java.util.Objects;

public record HexColor(int red, int green, int blue) {

    public HexColor {
        validateChannel(red, "red");
        validateChannel(green, "green");
        validateChannel(blue, "blue");
    }

    public static HexColor of(String value) {
        Objects.requireNonNull(value, "value");

        String hex = value.trim().toLowerCase(Locale.ROOT);

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }

        if (hex.length() == 3) {
            hex = expandShortHex(hex);
        }

        if (hex.length() != 6) {
            throw new IllegalArgumentException(
                    "Invalid hex color '" + value + "'. Expected #RGB or #RRGGBB."
            );
        }

        try {
            int rgb = Integer.parseInt(hex, 16);

            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

            return new HexColor(red, green, blue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid hex color '" + value + "'.",
                    exception
            );
        }
    }

    public static HexColor of(int red, int green, int blue) {
        return new HexColor(red, green, blue);
    }

    public static HexColor fromRgbInt(int rgb) {
        return new HexColor(
                (rgb >> 16) & 0xFF,
                (rgb >> 8) & 0xFF,
                rgb & 0xFF
        );
    }

    public static HexColor lerp(HexColor from, HexColor to, double progress) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");

        double t = clamp(progress, 0.0, 1.0);

        return new HexColor(
                lerpChannel(from.red, to.red, t),
                lerpChannel(from.green, to.green, t),
                lerpChannel(from.blue, to.blue, t)
        );
    }

    public TextColor adventure() {
        return TextColor.color(red, green, blue);
    }

    public int rgb() {
        return (red << 16) | (green << 8) | blue;
    }

    public String hex() {
        return String.format("#%02x%02x%02x", red, green, blue);
    }

    public String miniMessageTag() {
        return "<" + hex() + ">";
    }

    public String legacySection() {
        String hex = hex().substring(1);

        StringBuilder builder = new StringBuilder("§x");

        for (char character : hex.toCharArray()) {
            builder.append('§').append(character);
        }

        return builder.toString();
    }

    private static int lerpChannel(int from, int to, double progress) {
        return (int) Math.round(from + ((to - from) * progress));
    }

    private static String expandShortHex(String hex) {
        return ""
                + hex.charAt(0) + hex.charAt(0)
                + hex.charAt(1) + hex.charAt(1)
                + hex.charAt(2) + hex.charAt(2);
    }

    private static void validateChannel(int value, String name) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(
                    "Color channel '" + name + "' must be between 0 and 255."
            );
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
