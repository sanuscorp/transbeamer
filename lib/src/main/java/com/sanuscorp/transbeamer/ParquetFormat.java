package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The {@link DataFormat} implementation that understands how to read and write
 * Parquet files.
 */
public final class ParquetFormat implements DataFormat {

    private ParquetFormat() {
        // Intentionally empty
    }

    public static ParquetFormat create() {
        return new ParquetFormat();
    }

    @Override
    public String getName() {
        return "Parquet";
    }

    @Override
    public String getSuffix() {
        return "parquet";
    }

    @Override
    public <T extends SpecificRecordBase> PTransform<
        @NonNull PBegin,
        @NonNull PCollection<T>
    > getReader(
        final String dataPattern,
        final Class<T> clazz
    ) {
        return ParquetReader.read(dataPattern, clazz);
    }

    @Override
    public <T extends GenericRecord> FileIO.Sink<T> getWriter(
        final Class<T> clazz
    ) {
        return ParquetSink.of(clazz);
    }
}
