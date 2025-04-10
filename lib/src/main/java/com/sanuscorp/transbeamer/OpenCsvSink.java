package com.sanuscorp.transbeamer;

import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.io.FileIO;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;


public class OpenCsvSink<T extends GenericRecord> implements FileIO.Sink<T> {

    private final Class<T> clazz;

    /**
     * The OpenCsv class we use to write through
     */
    private StatefulBeanToCsv<T> beanToCsv;

    /**
     * The Java writer we use to write through.
     */
    private java.io.Writer writer;

    private OpenCsvSink(final Class<T> clazz) {
        this.clazz = clazz;
    }

    static <T extends GenericRecord> OpenCsvSink<T> of(final Class<T> clazz) {
        return new OpenCsvSink<>(clazz);
    }

    @Override
    public void open(WritableByteChannel channel) {
        this.writer = Channels.newWriter(channel, StandardCharsets.UTF_8);

        final MappingStrategy<T> strategy = OpenCsvAvroMappingStrategy.of(clazz);
        beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
            .withMappingStrategy(strategy)
            .withOrderedResults(false)
            .build();
    }

    @Override
    public void write(T element) throws IOException {
        try {
            beanToCsv.write(element);
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            throw new IOException(
                "Failed to write CSV element", e
            );
        }
    }

    @Override
    public void flush() throws IOException {
        writer.close();
    }
}
