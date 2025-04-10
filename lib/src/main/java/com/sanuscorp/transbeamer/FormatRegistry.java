package com.sanuscorp.transbeamer;

import java.util.*;

// TODO: delete this as it currently stands; will be replaced later
public final class FormatRegistry {

    private static final Map<String, FileFormat> FORMAT_MAP = new HashMap<>();
    static {
        Arrays.stream(BuiltInFormat.values()).forEach(
            f -> addFormat(f.getFormat())
        );
    }

    public static void addFormat(final FileFormat format) {
        FORMAT_MAP.put(format.getName(), format);
    }

    public static Set<String> getKeys() {
        return FORMAT_MAP.keySet();
    }

    public static FileFormat getFormat(final String formatName) {
        final FileFormat format = FORMAT_MAP.get(formatName);
        if (format == null) {
            throw new IllegalArgumentException(
                "No format: "
                    + formatName
                    + "; supported formats: "
                    + FORMAT_MAP.keySet()
            );
        }
        return format;
    }
}
