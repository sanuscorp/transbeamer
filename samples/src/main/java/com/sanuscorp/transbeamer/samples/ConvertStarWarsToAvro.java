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
 * and writes it as Avro to the build directory.
 */
public class ConvertStarWarsToAvro {

    /**
     * Main entry point for the ConvertStarWarsToAvro application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Converting StarWars.parquet to Avro format...");

        // Ensure the output directory exists
        Path outputPath = Paths.get("build");
        if (Files.notExists(outputPath)) {
            Files.createDirectories(outputPath);
        }

        // Create a pipeline
        Pipeline pipeline = Pipeline.create();

        // Transform the Parquet file to an Avro file
        pipeline.apply(
            TransBeamer.newReader(
                BuiltInFormat.PARQUET,
                    "build",
                    StarWarsMovie.class
                )
                .withFilePrefix("StarWars")
        ).apply(
            TransBeamer.newWriter(
                BuiltInFormat.AVRO,
                    "build",
                    StarWarsMovie.class
            )
                .withNumShards(1)
                .withFilePrefix("StarWars")
        );

        // Run the pipeline
        pipeline.run().waitUntilFinish();

        System.out.println("Conversion completed successfully!");
        System.out.println("Avro file created at: build/StarWarsAvro-00000-of-00001.avro");
    }
}