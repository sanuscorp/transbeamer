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
        
        This current supports the following formats:\s
          - Avro\s
          - Parquet\s
          - CSV\s
          - ND-JSON\s
        
        The "samples" project provides apps that produce different\s
        file formats in the "samples/builddirectory.\s
        """);

        System.out.println("\nFor Instance:");
        System.out.println("----------------------------");
        System.out.println("CSV ");
        System.out.println("   => Parquet");
        System.out.println("   => Avro");
        System.out.println("  ./gradlew toAvroAndParquet");
    }
}
