package com.sanuscorp.transbeamer;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.io.FileIO;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;

public class NDJsonWriter<T extends GenericRecord> implements FileIO.Sink<T> {

    private final Class<T> clazz;

    private Gson gson;

    private OutputStreamWriter writer;

    public NDJsonWriter(final Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <T extends GenericRecord> NDJsonWriter<T> of(
        final Class<T> clazz
    ) {
        return new NDJsonWriter<>(clazz);
    }

    @Override
    public void open(WritableByteChannel channel) {
        gson = new GsonBuilder()
            .setFormattingStyle(FormattingStyle.COMPACT)
            .create();

        final OutputStream stream = Channels.newOutputStream(channel);
        writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
    }

    @Override
    public void write(T element) throws IOException {
        gson.toJson(element, clazz, writer);
        writer.write("\n");
    }

    @Override
    public void flush() throws IOException {
        if (writer != null) {
            writer.flush();
        }
    }
}
