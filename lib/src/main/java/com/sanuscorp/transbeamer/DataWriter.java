package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class provides the ability to write files in a variety of formats.
 * @param <T> The element type that will be provided when running.
 */
public final class DataWriter<T extends GenericRecord> extends PTransform<
    @NonNull PCollection<T>,
    @NonNull PDone
> {
    private static final Logger LOG = LogManager.getLogger(DataWriter.class);

    private final Class<T> clazz;

    private final String outputLocation;

    private final FileFormat format;

    private String filePrefix = "";

    private Integer numShards;

    DataWriter(
        final FileFormat format,
        final String outputLocation,
        final Class<T> clazz
    ) {
        this.format = format;
        this.outputLocation = outputLocation;
        this.clazz = clazz;
    }

    @Override
    public @NonNull PDone expand(@NonNull final PCollection<T> input) {

        final String suffix = "." + format.getSuffix();
        FileIO.Write<Void, T> writer = FileIO.<T>write()
            .to(outputLocation)
            .withPrefix(filePrefix)
            .withSuffix(suffix);

        if (this.numShards != null) {
            writer = writer.withNumShards(this.numShards);
        }

        final FileIO.Sink<T> sink = format.getWriter(clazz);

        LOG.debug("Writing {} instances to {}/{}*{}",
            clazz.getSimpleName(),
            outputLocation,
            filePrefix,
            suffix
        );
        input.apply(
            "Write to " + outputLocation + "/" + filePrefix + "*" + suffix,
            writer.via(sink)
        );

        return PDone.in(input.getPipeline());
    }

    public DataWriter<T> withFilePrefix(final String filePrefix) {
        this.filePrefix = filePrefix;
        return this;
    }

    public DataWriter<T> withNumShards(final Integer numShards) {
        this.numShards = numShards;
        return this;
    }
}
