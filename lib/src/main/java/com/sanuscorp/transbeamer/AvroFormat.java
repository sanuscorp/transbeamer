package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.extensions.avro.io.AvroIO;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class provides the {@link DataFormat} for reading and writing Avro
 * files.
 */
public final class AvroFormat implements DataFormat {

    private AvroFormat() {
        // Intentionally empty
    }

    public static AvroFormat create() {
        return new AvroFormat();
    }

    @Override
    public String getName() {
        return "Avro";
    }

    @Override
    public String getSuffix() {
        return "avro";
    }

    @Override
    public <T extends SpecificRecordBase> PTransform<
        @NonNull PBegin,
        @NonNull PCollection<T>
    > getReader(final String dataPattern, final Class<T> clazz) {
        return AvroReader.read(dataPattern, clazz);
    }

    @Override
    public <
        T extends GenericRecord
    > FileIO.Sink<T> getWriter(final Class<T> clazz) {
        return AvroIO.sink(clazz);
    }
}
