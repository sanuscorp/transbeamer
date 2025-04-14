package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.AvroFormat;
import com.sanuscorp.transbeamer.ParquetFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;

/**
 * This application reads the StarWars parquet file from the build directory
 * and writes it as Avro to the build directory.
 */
public class ConvertParquetToAvro {

    /**
     * Main entry point for the ConvertStarWarsToAvro application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Converting StarWars.parquet to Avro format...");

        // Create a pipeline
        Pipeline pipeline = Pipeline.create();

        // Transform the Parquet file to an Avro file
        pipeline.apply(
            TransBeamer.newReader(
                ParquetFormat.create(),
                "build",
                StarWarsMovie.class
            ).withFilePrefix("StarWars")
        ).apply(
            TransBeamer.newWriter(
                AvroFormat.create(),
                "build",
                StarWarsMovie.class
            ).withNumShards(1)
                .withFilePrefix("StarWars")
        );

        // Run the pipeline
        pipeline.run().waitUntilFinish();

        System.out.println("Avro file created at: build/StarWarsAvro-*.avro");
    }
}