package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This interface defines the type that enables reading and writing of file
 * formats.
 */
public interface FileFormat {

    String getName();

    String getSuffix();

    <T extends SpecificRecordBase> PTransform<
        @NonNull PBegin,
        @NonNull PCollection<T>
    > getReader(String filePattern, Class<T> clazz);

    <T extends GenericRecord> FileIO.Sink<T> getWriter(
        Class<T> clazz
    );
}
