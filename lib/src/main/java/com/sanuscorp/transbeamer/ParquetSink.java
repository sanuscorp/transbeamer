package com.sanuscorp.transbeamer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.parquet.ParquetIO;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

/**
 * This class provides a {@link FileIO.Sink} implementation that wraps around
 * a {@link ParquetIO.Sink} instance.
 * @param <T> The type that will be written out to Parquet.
 */
public final class ParquetSink<
    T extends GenericRecord
> implements FileIO.Sink<T> {

    /**
     * The ParquetIO.Sink instance we are wrapping around.
     */
    private final ParquetIO.Sink parquetIOSink;

    private ParquetSink(final Class<T> clazz) {
        final Schema avroSchema = ReflectUtils.getClassSchema(clazz);
        parquetIOSink = ParquetIO.sink(avroSchema);
    }

    static <T extends GenericRecord> ParquetSink<T> of(final Class<T> clazz) {
        return new ParquetSink<>(clazz);
    }

    @Override
    public void open(final WritableByteChannel channel) throws IOException {
        this.parquetIOSink.open(channel);
    }

    @Override
    public void write(final T element) throws IOException {
        this.parquetIOSink.write(element);
    }

    @Override
    public void flush() throws IOException {
        this.parquetIOSink.flush();
    }
}
