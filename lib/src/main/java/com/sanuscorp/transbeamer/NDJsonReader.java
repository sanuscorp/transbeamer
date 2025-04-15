package com.sanuscorp.transbeamer;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.extensions.avro.coders.AvroCoder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class implements a {@link PTransform} that knows how to read
 * Newline-delimited JSON files.
 * @param <T> The type that we will create the ND-JSON into.
 */
public final class NDJsonReader<T> extends PTransform<
    @NonNull PBegin,
    @NonNull PCollection<T>
> {
    private final String filePattern;

    private final Class<T> clazz;

    private NDJsonReader(final String filePattern, final Class<T> clazz) {
        this.filePattern = filePattern;
        this.clazz = clazz;
    }

    static <T> NDJsonReader<T> read(
        final String filePattern,
        final Class<T> clazz
    ) {
        return new NDJsonReader<>(filePattern, clazz);
    }

    @Override
    public @NonNull PCollection<T> expand(@NonNull final PBegin input) {
        final Pipeline pipeline = input.getPipeline();

        final PCollection<String> lines = pipeline.apply(
            "Reading Files",
            TextIO.read().from(filePattern)
        );

        return lines.apply(
            "Reading NDJson: " + filePattern,
            NDJsonReaderFn.parDoOf(clazz)
        ).setCoder(AvroCoder.of(clazz));
    }
}
