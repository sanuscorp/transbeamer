package com.sanuscorp.transbeamer.samples;

/**
 * A class to provide a welcome message.
 */
public class Welcome {

    /**
     * Entry point for the Welcome Gradle task.
     */
    public static void main(String[] args) {
        System.out.println("""
        Welcome! \s
        
        The TransBeamer library provides utilities for\s
        reading and writing data in various formats,\s
        populating Avro-based PCollections as interim values.\s
        
        This currently supports the following formats:\s
          - Avro\s
          - Parquet\s
          - CSV\s
          - ND-JSON\s
        
        The "samples" project provides apps that produce different\s
        file formats in the "samples/build" directory.\s
        """);

        System.out.println("""
        
        For Instance:
        -----------------------------------------------------
        See code that reads CSV, then writes out Parquet & Avro here:
        
          samples/src/main/java/.../ConvertCsvToAvroAndParquet.java\s
        
        To run that sample, run this Gradle task:
        
          ./gradlew convertCsvToAvroAndParquet
        
        """);
    }
}
