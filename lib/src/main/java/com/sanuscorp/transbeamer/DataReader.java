package com.sanuscorp.transbeamer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class provides the format-agnostic Reader transform.
 * @param <T> The element type that this transform will read into.
 */
public final class DataReader<T extends SpecificRecordBase> extends PTransform<
    @NonNull PBegin,
    @NonNull PCollection<T>
> {
    private static final Logger LOG = LogManager.getLogger(DataReader.class);

    private final DataFormat format;

    private final Class<T> clazz;

    DataReader(final DataFormat format, final Class<T> clazz) {
        this.format = format;
        this.clazz = clazz;
    }

    @Override
    public @NonNull PCollection<T> expand(@NonNull final PBegin input) {
        LOG.debug(
            "Reading data from {} into {}",
            format.getName(),
            clazz.getSimpleName()
        );

        final PTransform<@NonNull PBegin, @NonNull PCollection<T>> reader =
            format.getReader(clazz);

        return input.apply(
            "Read from " + format.getName(),
            reader
        );
    }
}
