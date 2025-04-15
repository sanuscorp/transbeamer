package com.sanuscorp.transbeamer;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

/**
 * This class provides the {@link DoFn} that can stream in a CSV file and
 * convert it to a collection of elements.
 * @param <B> The JavaBean type that will be created by this {@link DoFn}.
 */
public final class OpenCsvReaderFn<B> extends DoFn<
    FileIO.ReadableFile,
    B
> {
    private static final Logger LOG = LogManager.getLogger(
        OpenCsvReaderFn.class);

    private final Class<B> clazz;

    private OpenCsvReaderFn(final Class<B> clazz) {
        this.clazz = clazz;
    }

    /**
     * Create a {@link ParDo} that leverages an instance of this {@link DoFn}
     * to process each element.
     * @param clazz The class of elements to populate
     * @return The ParDo itself.
     * @param <B> The JavaBean type that will be created by this {@link DoFn}.
     */
    static <B> ParDo.SingleOutput<FileIO.ReadableFile, B> parDoOf(
        final Class<B> clazz
    ) {
        final OpenCsvReaderFn<B> converter = new OpenCsvReaderFn<>(clazz);
        return ParDo.of(converter);
    }

    @ProcessElement
    public void processElement(
        @Element final FileIO.ReadableFile file,
        final OutputReceiver<B> receiver
    ) throws IOException {
        final String filename = file.getMetadata().resourceId().getFilename();
        LOG.debug("Reading CSV file from {}", filename);
        final Reader reader = Channels.newReader(
            file.open(),
            StandardCharsets.UTF_8
        );

        final CsvToBean<B> csvToBean = new CsvToBeanBuilder<B>(reader)
            .withType(clazz)
            .build();

        csvToBean.stream().forEach(receiver::output);
    }
}
