package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This {@link FileFormat} provides the ability to read and write CSV files.
 * This implementation leverages `OpenCSV` to handle the details of CSV reading
 * and writing.
 */
public final class CsvFormat implements FileFormat {

    private CsvFormat() {
        // Intentionally empty
    }

    public static CsvFormat create() {
        return new CsvFormat();
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
    > getReader(final String dataPattern, final Class<T> clazz) {
        return OpenCsvReader.read(dataPattern, clazz);
    }

    @Override
    public <T extends GenericRecord> FileIO.Sink<T> getSink(
        final Class<T> clazz
    ) {
        return OpenCsvSink.of(clazz);
    }
}
