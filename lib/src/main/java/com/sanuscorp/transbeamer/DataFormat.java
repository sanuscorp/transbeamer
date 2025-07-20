package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This interface defines a type that enables reading and writing of data
 * sources and sinks.
 */
public interface DataFormat {

    /**
     * Get the name of the data format.  This is likely only useful for logging.
     * @return The name of the format.
     */
    String getName();

    /**
     * Get a data reader for this format.  Some data formats will build
     * new transforms, while others may delegate to Beam IO classes.
     * @param clazz The Avro-generated class to populate
     * @return The reader
     * @param <T> The Avro-generated class that will store elements consumed.
     */
    <T extends SpecificRecordBase> PTransform<
        @NonNull PBegin,
        @NonNull PCollection<T>
    > getReader(Class<T> clazz);


    /**
     * Get a data reader for this format.  Some data formats will build
     * new transforms, while others may delegate to Beam IO classes.
     * @param clazz The Avro-generated class to populate
     * @return The reader
     * @param <T> The Avro-generated class that will store elements consumed.
     */
    <T extends GenericRecord> PTransform<
        @NonNull PCollection<T>,
        @NonNull PDone
    > getWriter(Class<T> clazz);

}
