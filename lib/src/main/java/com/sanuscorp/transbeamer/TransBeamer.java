package com.sanuscorp.transbeamer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provides the main entry points into the library.
 */
public final class TransBeamer {

    private static final Logger LOG = LogManager.getLogger(TransBeamer.class);

    static {
        LOG.debug(
            "===== Loaded TransBeamer ====  \n\tCWD: {}",
            System.getProperty("user.dir")
        );
    }

    private TransBeamer() {
        // Intentionally Empty
    }

    /**
     * Create a new TransBeamer {@link FileReader} that can ingest data of a given
     * format at a given location into a given Avro class.
     * @param format The file format to read
     * @param location The location of the file(s) to read
     * @param clazz The class of the elements that will be in the resulting
     * PCollection.
     * @return A {@link FileReader} PTransform.
     * @param <T> The specific type of Avro element we will populate with the
     * data.
     */
    public static <T extends SpecificRecordBase> FileReader<T> newReader(
        final FileFormat format,
        final String location,
        final Class<T> clazz
    ) {
        return new FileReader<>(format, location, clazz);
    }

    /**
     * Create a new TransBeamer {@link FileWriter} that can produce data of a given
     * format at a given location from a given Avro class.
     * @param format The file format to write
     * @param location The location to write the file(s)
     * @param clazz The class of elements that will be provided via a
     * {@link org.apache.beam.sdk.values.PCollection} instance.
     * @return A {@link FileWriter} PTransform.
     * @param <T> The specific type of Avro element that will be written out.
     */
    public static <T extends SpecificRecordBase> FileWriter<T> newWriter(
        final FileFormat format,
        final String location,
        final Class<T> clazz
    ) {
        return new FileWriter<>(format, location, clazz);
    }
}
