package no.hvl.past.util;


import com.google.common.base.CaseFormat;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringUtils {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

    public enum StringCombinationStrategy {
        /** camelCase, i.e. capitalize after the first word (common in Java, C, JavaScript) */
        CAMEL_CASE,
        /** PascalCase, i.e. capitalize all words including the first */
        PASCAL_CASE,
        /** kebap-case, i.e. insert dash between each lowercased word (common for file names, CSS property names etc.) */
        KEBAP_CASE,
        /** snake_case, i.e. insert a underscore between each lowercased word (common in python, SQL etc.) */
        SNAKE_CASE,
        /** CONSTANT_CASE, i.e. insert a underscore between uppercased word (common for constant in C and Java) */
        CONSTANT_CASE
    }

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

    public static <T> String fuseList(Stream<T> elements, Function<T, String> toString, String separator) {
        return fuseList(elements.map(toString), separator);
    }

    public static String fuseList(Stream<String> elements, String separator) {
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

    public static <T> String fuseList(Collection<T> elements, Function<T, String> toString, String separator) {
        StringBuilder result = new StringBuilder();
        Iterator<T> iterator = elements.iterator();
        while (iterator.hasNext()) {
            String current = toString.apply(iterator.next());
            result.append(current);
            if (iterator.hasNext()) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static String fuseName(Collection<String> names, StringCombinationStrategy strategy) {
        String preResult = fuseList(names, String::toLowerCase, "_");
        switch (strategy) {
            case CAMEL_CASE:
                return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, preResult);
            case KEBAP_CASE:
                return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, preResult);
            case PASCAL_CASE:
                return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, preResult);
            case CONSTANT_CASE:
                return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, preResult);
            case SNAKE_CASE:
            default:
                return preResult;
        }
    }

    public static String capitalizeFirst(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static String lowerCaseFirst(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    public static String initials(String name) {
        if (name.contains(":")) {
            // probably a URL/URI
            String rest = name.replaceAll("//", "");
            rest = rest.replaceAll("www.", "");


        } else {
            if (name.contains(",")) {
                int idx = name.indexOf(',');
                char a = name.charAt(0);
                if (name.length() > idx + 1) {
                    char b = name.charAt(idx + 1);
                    return "" + b + a;
                } else {
                    return "" + a;
                }
            } else if (name.contains(" ")) {
                StringBuilder result = new StringBuilder();
                for (String part : name.split(" ")) {
                    result.append(part.charAt(0));
                }
                return result.toString();
            } else {

            }
        }

        return name; // TODO tries to compute a meaningful initial of the given String
    }

    public static boolean isNumber(String string) {
        return NUMBER_PATTERN.matcher(string).matches();
    }


}
