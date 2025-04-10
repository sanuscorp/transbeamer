package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.FormatRegistry;

/**
 * A simple Hello World application demonstrating the TransBeamer library.
 */
public class Welcome {

    /**
     * Main entry point for the HelloWorld sample application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello, TransBeamer World!");
        System.out.println("The TransBeamer library provides utilities for " +
            "reading and writing data in various formats.");

        // Display information about the TransBeamer library
        System.out.println("\nSome Samples:");
        System.out.println("----------------------------");
        System.out.println("Generate a Parquet file from memory:");
        System.out.println("  ./gradlew :samples:createParquet");
        System.out.println("Convert a Parquet file to CSV:");
        System.out.println("  ./gradlew :samples:convertParquetToCsv");
        System.out.println("Convert a CSV file to Avro:");
        System.out.println("  ./gradlew :samples:convertCsvToAvro");

        System.out.println("\nRegistered file formats:");
        for (String key : FormatRegistry.getKeys()) {
            System.out.println("  - " + key);
        }
    }
}
