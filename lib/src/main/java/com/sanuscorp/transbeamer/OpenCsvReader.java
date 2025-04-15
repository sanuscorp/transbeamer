package com.sanuscorp.transbeamer;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.extensions.avro.coders.AvroCoder;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This class provides the {@link PTransform} that understands how to read CSV
 * files.  This implementation leverages the <code>OpenCsv</code> library
 * for the lower-level bits.
 * @param <B> The Bean type that will be populated from the CSV file.
 */
public final class OpenCsvReader<B> extends PTransform<
    @NonNull PBegin,
    @NonNull PCollection<B>
> {
    private final String filePattern;

    private final Class<B> clazz;

    private OpenCsvReader(final String filePattern, final Class<B> clazz) {
        this.filePattern = filePattern;
        this.clazz = clazz;
    }

    static <B> OpenCsvReader<B> read(
        final String filePattern,
        final Class<B> clazz
    ) {
        return new OpenCsvReader<>(filePattern, clazz);
    }

    @Override
    public @NonNull PCollection<B> expand(@NonNull final PBegin input) {
        final Pipeline pipeline = input.getPipeline();

        final PCollection<MatchResult.Metadata> metadata = pipeline.apply(
            "Finding files: " + filePattern,
            FileIO.match().filepattern(filePattern)
        );

        final PCollection<FileIO.ReadableFile> readableFiles = metadata.apply(
            "Reading metadata: " + filePattern,
            FileIO.readMatches()
        );

        return readableFiles.apply(
            "Reading CSV: " + filePattern,
            OpenCsvReaderFn.parDoOf(clazz)
        ).setCoder(AvroCoder.of(clazz));
    }
}
