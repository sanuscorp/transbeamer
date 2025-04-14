package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.CsvFormat;
import com.sanuscorp.transbeamer.NDJsonFormat;
import com.sanuscorp.transbeamer.ParquetFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;

public class ConvertNDJsonToCSV {
    public static void main(String[] args) {
        System.out.println("Converting StarWars.parquet to CSV...");

        // Read in Parquet, write out CSV
        Pipeline pipeline = Pipeline.create();
        pipeline.apply(
            TransBeamer.newReader(
                    new NDJsonFormat(),
                    "build",
                    StarWarsMovie.class
                )
                .withFilePrefix("StarWars")
        ).apply(
            TransBeamer.newWriter(
                    new CsvFormat(),
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
