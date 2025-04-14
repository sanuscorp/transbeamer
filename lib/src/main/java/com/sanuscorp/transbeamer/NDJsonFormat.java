package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class NDJsonFormat implements FileFormat {

    public static NDJsonFormat create() {
        return new NDJsonFormat();
    }

    private NDJsonFormat() {
        // Intentionally empty
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
    > getReader(String filePattern, Class<T> clazz) {
        return NDJsonReader.read(filePattern, clazz);
    }

    @Override
    public <T extends GenericRecord> FileIO.Sink<T> getWriter(Class<T> clazz) {
        return NDJsonWriter.of(clazz);
    }
}
