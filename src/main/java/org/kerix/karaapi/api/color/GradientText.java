package org.kerix.karaapi.api.color;

import net.kyori.adventure.text.Component;

public final class GradientText {

    private GradientText() {
    }

    public static Component component(String text, String from, String to) {
        return ColorGradient.of(from, to).component(text);
    }

    public static Component component(String text, String... colors) {
        return ColorGradient.of(colors).component(text);
    }

    public static String legacy(String text, String from, String to) {
        return ColorGradient.of(from, to).legacy(text);
    }

    public static String legacy(String text, String... colors) {
        return ColorGradient.of(colors).legacy(text);
    }

    public static String miniMessage(String text, String from, String to) {
        return ColorGradient.of(from, to).miniMessage(text);
    }

    public static String miniMessage(String text, String... colors) {
        return ColorGradient.of(colors).miniMessage(text);
    }
}
