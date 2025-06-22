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
     * Create a new TransBeamer {@link DataReader} that can ingest data of a given
     * format at a given location into a given Avro class.
     * @param format The file format to read
     * @param location The location of the file(s) to read
     * @param clazz The class of the elements that will be in the resulting
     * PCollection.
     * @return A {@link DataReader} PTransform.
     * @param <T> The specific type of Avro element we will populate with the
     * data.
     */
    public static <T extends SpecificRecordBase> DataReader<T> newReader(
        final DataFormat format,
        final String location,
        final Class<T> clazz
    ) {
        return new DataReader<>(format, location, clazz);
    }

    /**
     * Create a new TransBeamer {@link DataWriter} that can produce data of a given
     * format at a given location from a given Avro class.
     * @param format The file format to write
     * @param location The location to write the file(s)
     * @param clazz The class of elements that will be provided via a
     * {@link org.apache.beam.sdk.values.PCollection} instance.
     * @return A {@link DataWriter} PTransform.
     * @param <T> The specific type of Avro element that will be written out.
     */
    public static <T extends SpecificRecordBase> DataWriter<T> newWriter(
        final DataFormat format,
        final String location,
        final Class<T> clazz
    ) {
        return new DataWriter<>(format, location, clazz);
    }
}
