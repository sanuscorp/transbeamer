package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This {@link FileFormat} implementation provides the ability to read and write
 * Newline-Delimited JSON files.
 */
public final class NDJsonFormat implements FileFormat {

    private NDJsonFormat() {
        // Intentionally empty
    }

    public static NDJsonFormat create() {
        return new NDJsonFormat();
    }

    @Override
    public String getName() {
        return "ND-JSON";
    }

    @Override
    public String getSuffix() {
        return "ndjson";
    }

    @Override
    public <T extends SpecificRecordBase> PTransform<
        @NonNull PBegin,
        @NonNull PCollection<T>
    > getReader(final String filePattern, final Class<T> clazz) {
        return NDJsonReader.read(filePattern, clazz);
    }

    @Override
    public <T extends GenericRecord> FileIO.Sink<T> getWriter(
        final Class<T> clazz
    ) {
        return NDJsonWriter.of(clazz);
    }
}
