package com.sanuscorp.transbeamer.samples;

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
        System.out.println("Memory => Parquet:");
        System.out.println("  ./gradlew createParquet");
        System.out.println("Memory => Parquet => CSV:");
        System.out.println("  ./gradlew convertParquetToCsv");
        System.out.println("Memory => Parquet => CSV => Avro:");
        System.out.println("  ./gradlew convertCsvToAvro");
        System.out.println("Memory => Parquet => ND-JSON:");
        System.out.println("  ./gradlew convertParquetToNDJson");
        System.out.println("Memory => Parquet => ND-JSON => CSV:");
        System.out.println("  ./gradlew convertNDJsonToCSV");
    }
}
