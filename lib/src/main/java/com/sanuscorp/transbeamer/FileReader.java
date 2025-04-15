package com.sanuscorp.transbeamer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class provides the Reader transform.
 * @param <T> The element type that we will read data into.
 */
public final class FileReader<T extends SpecificRecordBase> extends PTransform<
    @NonNull PBegin,
    @NonNull PCollection<T>
> {
    private static final Logger LOG = LogManager.getLogger(FileReader.class);

    private final String location;

    private final FileFormat format;

    private String filePrefix = "";

    private final Class<T> clazz;

    FileReader(
        final FileFormat format,
        final String inputLocation,
        final Class<T> clazz
    ) {
        this.format = format;
        this.clazz = clazz;
        this.location = inputLocation;
    }

    public FileReader<T> withFilePrefix(final String filePrefix) {
        this.filePrefix = filePrefix;
        return this;
    }

    @Override
    public @NonNull PCollection<T> expand(@NonNull final PBegin input) {
        final String locationBase;
        if (location.endsWith("/")) {
            locationBase = location + filePrefix;
        } else {
            locationBase = location + "/" + filePrefix;
        }
        final String filePattern = locationBase + "*." + format.getSuffix();

        final PTransform<
            @NonNull PBegin,
            @NonNull PCollection<T>
        > reader = format.getReader(filePattern, clazz);

        LOG.debug("Reading {} into {}", filePattern, clazz.getSimpleName());
        return input.apply(reader);
    }
}
