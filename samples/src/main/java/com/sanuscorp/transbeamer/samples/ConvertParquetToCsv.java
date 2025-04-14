package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.CsvFormat;
import com.sanuscorp.transbeamer.ParquetFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;

/**
 * This application reads the StarWars parquet file from the build directory
 * and writes it as CSV to the build directory.
 */
public class ConvertParquetToCsv {

    /**
     * Main entry point for the ConvertStarWarsToCsv application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Converting StarWars.parquet to CSV...");

        // Read in Parquet, write out CSV
        Pipeline pipeline = Pipeline.create();
        pipeline.apply(
            TransBeamer.newReader(
                ParquetFormat.create(),
                "build",
                StarWarsMovie.class
            )
            .withFilePrefix("StarWars")
        ).apply(
            TransBeamer.newWriter(
                CsvFormat.create(),
                "build",
                StarWarsMovie.class
            )
                .withNumShards(1)
                .withFilePrefix("StarWars")
        );
        pipeline.run().waitUntilFinish();

        System.out.println("CSV file created at: build/StarWarsCsv-*.csv");
    }
}