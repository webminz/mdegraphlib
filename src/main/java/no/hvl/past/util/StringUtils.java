package no.hvl.past.util;

import java.util.stream.Stream;

public class StringUtils {

    public static String produceIndentation(int level) {
        if (level < 0) {
            return "";
        }
        switch (level) {
            case 0:
                return "";
            case 1:
                return "   ";
            case 2:
                return "      ";
            case 3:
                return "         ";
            case 4:
                return "            ";
            case 5:
                return "               ";
            case 6:
                return "                  ";
            default:
                return "   " + produceIndentation(level-1);
        }
    }

    public static String fuse(Stream<String> elements, String separator) {
        StringBuilder result = new StringBuilder();
        elements.forEach(el -> {
            result.append(el);
            result.append(separator);
        });
        if (result.length() > separator.length()) {
            result.delete(result.length() - separator.length(), result.length());
        }
        return result.toString();
    }


}
