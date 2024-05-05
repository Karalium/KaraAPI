package org.kerix.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConvertList {
    public static List<Object> array(Object... objects){
        return new ArrayList<>(Arrays.asList(objects));
    }
    public static List<?> type(List<?> list, Class<?> type) {
        if (type == String.class) {
            return list.stream().map(obj -> (String) obj).toList();
        } else if (type == Double.class) {
            return list.stream().map(obj -> (Double) obj).toList();
        } else if (type == Integer.class) {
            return list.stream().map(obj -> (Integer) obj).toList();
        } else {
            return new ArrayList<>(list);
        }
    }

}
