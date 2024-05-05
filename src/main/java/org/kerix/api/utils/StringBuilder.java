package org.kerix.api.utils;

import net.kyori.adventure.text.Component;

public class StringBuilder {


    public static Component buildString(String str) {
        return Component.text(str.replace("&", "§"));
    }

}

