package org.kerix.karaapi.api.color;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ColorGradient {

    private final List<HexColor> stops;

    private ColorGradient(List<HexColor> stops) {
        if (stops == null || stops.isEmpty()) {
            throw new IllegalArgumentException("A gradient needs at least one color.");
        }

        this.stops = List.copyOf(stops);
    }

    public static ColorGradient of(String... colors) {
        Objects.requireNonNull(colors, "colors");

        List<HexColor> stops = new ArrayList<>();

        for (String color : colors) {
            stops.add(HexColor.of(color));
        }

        return new ColorGradient(stops);
    }

    public static ColorGradient of(HexColor... colors) {
        Objects.requireNonNull(colors, "colors");
        return new ColorGradient(Arrays.asList(colors));
    }

    public List<HexColor> stops() {
        return stops;
    }

    public static ColorGradient solid(String color) {
        return new ColorGradient(List.of(HexColor.of(color)));
    }

    public HexColor colorAt(double progress) {
        double t = clamp(progress, 0.0, 1.0);

        if (stops.size() == 1) {
            return stops.getFirst();
        }

        int segmentCount = stops.size() - 1;

        double scaled = t * segmentCount;
        int segmentIndex = (int) Math.floor(scaled);

        if (segmentIndex >= segmentCount) {
            return stops.getLast();
        }

        double localProgress = scaled - segmentIndex;

        HexColor from = stops.get(segmentIndex);
        HexColor to = stops.get(segmentIndex + 1);

        return HexColor.lerp(from, to, localProgress);
    }

    public String hexAt(double progress) {
        return colorAt(progress).hex();
    }

    public Component component(String text) {
        return component(text, false);
    }

    public Component component(String text, boolean colorSpaces) {
        Objects.requireNonNull(text, "text");

        int[] codePoints = text.codePoints().toArray();
        int coloredLength = countColoredCharacters(codePoints, colorSpaces);

        if (coloredLength == 0) {
            return Component.text(text);
        }

        Component result = Component.empty();

        int coloredIndex = 0;

        for (int codePoint : codePoints) {
            String character = new String(Character.toChars(codePoint));

            if (!colorSpaces && Character.isWhitespace(codePoint)) {
                result = result.append(Component.text(character));
                continue;
            }

            double progress = progressOf(coloredIndex, coloredLength);
            HexColor color = colorAt(progress);

            result = result.append(Component.text(character, color.adventure()));

            coloredIndex++;
        }

        return result;
    }

    public String miniMessage(String text) {
        return miniMessage(text, false);
    }

    public String miniMessage(String text, boolean colorSpaces) {
        Objects.requireNonNull(text, "text");

        int[] codePoints = text.codePoints().toArray();
        int coloredLength = countColoredCharacters(codePoints, colorSpaces);

        if (coloredLength == 0) {
            return text;
        }

        StringBuilder builder = new StringBuilder();

        int coloredIndex = 0;

        for (int codePoint : codePoints) {
            String character = new String(Character.toChars(codePoint));

            if (!colorSpaces && Character.isWhitespace(codePoint)) {
                builder.append(character);
                continue;
            }

            double progress = progressOf(coloredIndex, coloredLength);
            HexColor color = colorAt(progress);

            builder.append(color.miniMessageTag()).append(character);

            coloredIndex++;
        }

        return builder.toString();
    }

    public String legacy(String text) {
        return legacy(text, false);
    }

    public String legacy(String text, boolean colorSpaces) {
        Objects.requireNonNull(text, "text");

        int[] codePoints = text.codePoints().toArray();
        int coloredLength = countColoredCharacters(codePoints, colorSpaces);

        if (coloredLength == 0) {
            return text;
        }

        StringBuilder builder = new StringBuilder();

        int coloredIndex = 0;

        for (int codePoint : codePoints) {
            String character = new String(Character.toChars(codePoint));

            if (!colorSpaces && Character.isWhitespace(codePoint)) {
                builder.append(character);
                continue;
            }

            double progress = progressOf(coloredIndex, coloredLength);
            HexColor color = colorAt(progress);

            builder.append(color.legacySection()).append(character);

            coloredIndex++;
        }

        return builder.toString();
    }

    private int countColoredCharacters(int[] codePoints, boolean colorSpaces) {
        int count = 0;

        for (int codePoint : codePoints) {
            if (colorSpaces || !Character.isWhitespace(codePoint)) {
                count++;
            }
        }

        return count;
    }

    private double progressOf(int index, int length) {
        if (length <= 1) {
            return 0.0;
        }

        return index / (double) (length - 1);
    }

    private double clamp(double value, double min, double max) {
        return Math.clamp(value, min, max);
    }
}
