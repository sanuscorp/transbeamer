package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.NDJsonFormat;
import com.sanuscorp.transbeamer.ParquetFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;

public class ConvertParquetToNDJson {
    public static void main(String[] args) {
        System.out.println("Converting StarWars.parquet to ND-JSON format...");

        // Create a pipeline
        Pipeline pipeline = Pipeline.create();

        // Transform the Parquet file to an Avro file
        pipeline.apply(
            TransBeamer.newReader(
                    ParquetFormat.create(),
                    "build",
                    StarWarsMovie.class
                )
                .withFilePrefix("StarWars")
        ).apply(
            TransBeamer.newWriter(
                    NDJsonFormat.create(),
                    "build",
                    StarWarsMovie.class
                )
                .withNumShards(1)
                .withFilePrefix("StarWars")
        );

        // Run the pipeline
        pipeline.run().waitUntilFinish();

        System.out.println(
            "ND-JSON file created at: build/StarWarsAvro-*.ndjson"
        );
    }
}
