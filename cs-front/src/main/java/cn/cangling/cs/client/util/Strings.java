package cn.cangling.cs.client.util;

import com.google.gwt.i18n.client.NumberFormat;

public class Strings {
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static String trimToEmpty(String s) {
        if (s == null) {
            return "";
        }
        return s.trim();
    }

    public static String trimToNull(String s) {
        if (s == null) {
            return null;
        } else {
            return s.trim();
        }
    }

    public static String formatFileSize(Number fileSize) {
        if (fileSize == null) {
            return "";
        }
        double size = fileSize.doubleValue();
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return toFix(size / 1024, 2) + "K";
        } else if (size < 1024 * 1024 * 1024) {
            return toFix(size / 1024 / 1024, 2) + "M";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            return toFix(size / 1024 / 1024 / 1024, 2) + "G";
        } else {
            return toFix(size / 1024 / 1024 / 1024 / 1024, 2) + "T";
        }
    }

    public static String toFix(Number number, int precision) {
        if (number == null) {
            return "";
        }
        StringBuilder format = new StringBuilder();
        if (precision > 0) {
            format = new StringBuilder(".");
        }

        for (int i = 0; i < precision; ++i) {
            format.append("0");
        }

        NumberFormat df = NumberFormat.getFormat(format.toString());
        return df.format(number);
    }

}
