package com.sanuscorp.transbeamer;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.io.parquet.ParquetIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This file provides the ability to read Parquet files.
 * @param <T> The type the Parquet elements will be read into.
 */
public final class ParquetReader<T extends SpecificRecordBase> extends PTransform<
    @NonNull PBegin,
    @NonNull PCollection<T>
> {

    private final String filePattern;

    private final Class<T> clazz;

    private final Schema schema;

    private ParquetReader(final String filePattern, final Class<T> clazz) {
        this.filePattern = filePattern;
        this.clazz = clazz;
        this.schema = ReflectUtils.getClassSchema(clazz);
    }

    static <T extends SpecificRecordBase> ParquetReader<T> read(
        final String filePattern,
        final Class<T> clazz
    ) {
        return new ParquetReader<>(filePattern, clazz);
    }

    @Override
    public @NonNull PCollection<T> expand(@NonNull final PBegin input) {
        return input.apply(
            "Read Parquet from " + filePattern,
            ParquetIO.read(schema).from(filePattern)
        ).apply(
            "Convert to " + clazz.getName(),
            ConvertGenericToSpecificFn.parDoOf(clazz)
        );
    }
}
