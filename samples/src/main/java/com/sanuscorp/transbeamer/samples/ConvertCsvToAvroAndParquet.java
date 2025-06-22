package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.AvroFormat;
import com.sanuscorp.transbeamer.CsvFormat;
import com.sanuscorp.transbeamer.ParquetFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.values.PCollection;

public class ConvertCsvToAvroAndParquet {

    private static final String CWD = System.getProperty("user.dir");

    public static void main(String[] args) {
        System.out.println("Converting StarWars CSV to Avro...");
        Pipeline pipeline = Pipeline.create();

        /*
            Read from the "input" directory any files that match
            "starwars*.csv".
         */
        System.out.println("Reading CSV file from "
            + CWD
            + "/input/starwars*.csv");
        PCollection<StarWarsMovie> movies = pipeline.apply(
            TransBeamer.newReader(
                CsvFormat.create(),
                "input",
                StarWarsMovie.class
            ).withFilePrefix("starwars")
        );

        /*
            Write the collection out as both Avro files and Parquet files
            to the 'build' directory, prefixing each output with "StarWars*".
         */
        movies.apply(
            TransBeamer.newWriter(
                AvroFormat.create(),
                "build",
                StarWarsMovie.class
            )
                .withNumShards(1)
                .withFilePrefix("StarWars")
        );
        movies.apply(
            TransBeamer.newWriter(
                ParquetFormat.create(),
                "build",
                StarWarsMovie.class
            )
                .withNumShards(1)
                .withFilePrefix("StarWars")
        );

        pipeline.run().waitUntilFinish();

        System.out.println(
            "Avro file created at: " + CWD + "/build/StarWars-*.avro"
        );
        System.out.println(
            "Parquet file created at: " + CWD + "/build/StarWars-*.parquet"
        );

    }
}
