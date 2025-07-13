package com.sanuscorp.transbeamer;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.io.FileIO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;

/**
 * This class provides the {@link FileIO.Sink} implementation that understands
 * how to write Newline-Delimited JSON files.
 * @param <T> The type that will be written out as ND-JSON.
 */
public class NDJsonSink<T extends GenericRecord> implements FileIO.Sink<T> {

    private final Class<T> clazz;

    private Gson gson;

    private BufferedWriter bufferedWriter;

    public NDJsonSink(final Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <T extends GenericRecord> NDJsonSink<T> of(
        final Class<T> clazz
    ) {
        return new NDJsonSink<>(clazz);
    }

    @Override
    public void open(final WritableByteChannel channel) {
        gson = new GsonBuilder()
            .setStrictness(Strictness.STRICT)
            .setFormattingStyle(FormattingStyle.COMPACT)
            .create();

        final OutputStream stream = Channels.newOutputStream(channel);

        final OutputStreamWriter writer = new OutputStreamWriter(
            stream,
            StandardCharsets.UTF_8
        );
        bufferedWriter = new BufferedWriter(writer);

    }

    @Override
    public void write(final T element) throws IOException {
        gson.toJson(element, clazz, bufferedWriter);
        bufferedWriter.write(System.lineSeparator());
    }

    @Override
    public void flush() throws IOException {
        if (bufferedWriter != null) {
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }
}
