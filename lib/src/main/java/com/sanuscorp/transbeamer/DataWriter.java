package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class knows how to write data described in {@link DataFormat} types.
 * @param <T> The Avro type backing the {@link PCollection} holding the data to
 * be written.
 */
public class DataWriter<T extends GenericRecord> extends PTransform<
    @NonNull PCollection<T>,
    @NonNull PDone
> {
    private static final Logger LOG = LogManager.getLogger(DataWriter.class);

    private final DataFormat format;

    private final Class<T> clazz;

    DataWriter(final DataFormat format, final Class<T> clazz) {
        this.format = format;
        this.clazz = clazz;
    }

    @Override
    public @NonNull PDone expand(@NonNull final PCollection<T> input) {
        LOG.debug(
            "Writing {} instances to {}",
            clazz.getSimpleName(),
            format.getName()
        );

        final PTransform<
            @NonNull PCollection<T>,
            @NonNull PDone
        > writer = format.getWriter(clazz);

        return input.apply("Write to " + format.getName(), writer);
    }
}
