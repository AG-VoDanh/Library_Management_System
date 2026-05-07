package util;

public class MapperUtil {
    public static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
    public static Integer parseIntOrNull(String s) {
        return (s == null || s.isBlank()) ? null : Integer.parseInt(s);
    }
    public static String enumToString(Enum<?> e) {
        return e != null ? e.name() : null;
    }
    public static <E extends Enum<E>> E stringToEnum(String s, Class<E> clazz) {
        return (s != null) ? Enum.valueOf(clazz, s) : null;
    }
}
