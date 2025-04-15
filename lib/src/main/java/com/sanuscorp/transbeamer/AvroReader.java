package com.sanuscorp.transbeamer;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.extensions.avro.io.AvroIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class provides the {@link PTransform} that can read Avro files.
 * @param <T> The class type that will be populated from the Avro files.
 */
public final class AvroReader<T extends SpecificRecordBase> extends PTransform<
    @NonNull PBegin,
    @NonNull PCollection<T>
> {

    private final String filePattern;

    private final Class<T> clazz;

    private final Schema schema;

    private AvroReader(final String filePattern, final Class<T> clazz) {
        this.filePattern = filePattern;
        this.clazz = clazz;
        this.schema = ReflectUtils.getClassSchema(clazz);
    }

    static <T extends SpecificRecordBase> AvroReader<T> read(
        final String filePattern,
        final Class<T> clazz
    ) {
        return new AvroReader<>(filePattern, clazz);
    }

    @Override
    public @NonNull PCollection<T> expand(@NonNull final PBegin input) {
        return input.apply(
            "Read Avro from " + filePattern,
            AvroIO.readGenericRecords(schema).from(filePattern)
        ).apply(
            "Convert to " + clazz.getName(),
            ConvertGenericToSpecificFn.parDoOf(clazz)
        );
    }
}
