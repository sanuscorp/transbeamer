package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class CsvFormat implements FileFormat {

    public static CsvFormat create() {
        return new CsvFormat();
    }

    private CsvFormat() {
        // Intentionally empty
    }

    @Override
    public String getName() {
        return "CSV";
    }
    @Override
    public String getSuffix() {
        return "csv";
    }

    @Override
    public <T extends SpecificRecordBase> PTransform<
        @NonNull PBegin,
        @NonNull PCollection<T>
    > getReader(String filePattern, Class<T> clazz) {
        return OpenCsvReader.read(filePattern, clazz);
    }

    @Override
    public <T extends GenericRecord> FileIO.Sink<T> getWriter(
        Class<T> clazz) {
        return OpenCsvSink.of(clazz);
    }
}
