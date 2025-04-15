package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.CsvFormat;
import com.sanuscorp.transbeamer.NDJsonFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;

public class ConvertNDJsonToCSV {
    public static void main(String[] args) {
        System.out.println("Converting StarWars.ndjson to CSV...");

        // Read in Parquet, write out CSV
        Pipeline pipeline = Pipeline.create();
        pipeline.apply(
            TransBeamer.newReader(
                   NDJsonFormat.create(),
                    "build",
                    StarWarsMovie.class
                ).withFilePrefix("StarWars")
        ).apply(
            TransBeamer.newWriter(
                    CsvFormat.create(),
                    "build",
                    StarWarsMovie.class
                ).withNumShards(1)
                .withFilePrefix("StarWars")
        );
        pipeline.run().waitUntilFinish();

        System.out.println(
            "CSV file created at: build/StarWars-*.csv"
        );
    }
}
