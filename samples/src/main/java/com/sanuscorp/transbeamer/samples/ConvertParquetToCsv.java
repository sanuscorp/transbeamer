package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.BuiltInFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This application reads the StarWars parquet file from the build directory
 * and writes it as CSV to the build directory.
 */
public class ConvertParquetToCsv {

    /**
     * Main entry point for the ConvertStarWarsToCsv application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Converting StarWars.parquet to CSV...");

        // Ensure the output directory exists
        Path outputPath = Paths.get("build");
        if (Files.notExists(outputPath)) {
            Files.createDirectories(outputPath);
        }

        // Read in Parquet, write out CSV
        Pipeline pipeline = Pipeline.create();
        pipeline.apply(
            TransBeamer.newReader(
                BuiltInFormat.PARQUET,
                    "build",
                    StarWarsMovie.class
                )
                .withFilePrefix("StarWars")
        ).apply(
            TransBeamer.newWriter(
                BuiltInFormat.CSV,
                    "build",
                    StarWarsMovie.class
            )
                .withNumShards(1)
                .withFilePrefix("StarWars")
        );
        pipeline.run().waitUntilFinish();

        System.out.println("Conversion completed successfully!");
    }
}