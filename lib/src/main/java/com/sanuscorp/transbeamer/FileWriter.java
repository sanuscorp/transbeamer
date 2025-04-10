package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class FileWriter<T extends GenericRecord> extends PTransform<
    @NonNull PCollection<T>,
    @NonNull PDone
> {
    private static final Logger LOG = LogManager.getLogger(FileWriter.class);

    private final Class<T> clazz;

    private final String outputLocation;

    private final FileFormat format;

    private String filePrefix = "";

    private Integer numShards;

    FileWriter(
        final FileFormat format,
        final String outputLocation,
        final Class<T> clazz
    ) {
        this.format = format;
        this.outputLocation = outputLocation;
        this.clazz = clazz;
    }

    @Override
    public @NonNull PDone expand(@NonNull PCollection<T> input) {

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

    public FileWriter<T> withFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
        return this;
    }

    public FileWriter<T> withNumShards(Integer numShards) {
        this.numShards = numShards;
        return this;
    }
}
