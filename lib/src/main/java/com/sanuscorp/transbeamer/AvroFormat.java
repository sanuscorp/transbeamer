package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.extensions.avro.io.AvroIO;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AvroFormat implements FileFormat {

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
    > getReader(final String filePattern, final Class<T> clazz) {
        return AvroReader.read(filePattern, clazz);
    }

    @Override
    public <
        T extends GenericRecord
    > FileIO.Sink<T> getWriter(Class<T> clazz) {
        return AvroIO.sink(clazz);
    }
}
