package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.AvroFormat;
import com.sanuscorp.transbeamer.CsvFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;

public class ConvertCsvToAvro {

    public static void main(String[] args) {
        System.out.println("Converting StarWars CSV to Avro...");

        Pipeline pipeline = Pipeline.create();
        pipeline.apply(
            TransBeamer.newReader(
                CsvFormat.create(),
                "build",
                StarWarsMovie.class
            ).withFilePrefix("StarWars")
        ).apply(
            TransBeamer.newWriter(
                AvroFormat.create(),
                "build",
                StarWarsMovie.class
            )
                .withNumShards(1)
                .withFilePrefix("StarWars")
        );
        pipeline.run().waitUntilFinish();

        System.out.println(
            "Avro file created at: build/StarWarsAvro-*.avro"
        );

    }
}
