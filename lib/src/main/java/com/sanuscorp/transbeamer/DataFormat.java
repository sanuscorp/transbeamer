package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This interface defines the type that enables reading and writing of data
 * formats.
 */
public interface DataFormat {

    /**
     * The name of the data format.
     * @return The name
     */
    String getName();

    /**
     * The suffix of the data file to write, if any.
     * @return The suffix. May be the empty-length string.
     */
    String getSuffix();

    /**
     * Get a data reader for this format.  Some data formats will build
     * new transforms, while others may delegate to Beam IO classes.
     * @param dataPattern The pattern of the data to read.  This may be a
     * glob-like string.
     * @param clazz The Avro-generated class to populate
     * @return The reader
     * @param <T> The Avro-generated class that will store elements consumed.
     */
    <T extends SpecificRecordBase> PTransform<
        @NonNull PBegin,
        @NonNull PCollection<T>
    > getReader(String dataPattern, Class<T> clazz);

    <T extends GenericRecord> FileIO.Sink<T> getWriter(
        Class<T> clazz
    );
}
