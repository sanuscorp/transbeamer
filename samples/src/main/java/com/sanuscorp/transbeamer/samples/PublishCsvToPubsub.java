package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.CsvFormat;
import com.sanuscorp.transbeamer.PubsubFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.values.PCollection;

public class PublishCsvToPubsub {

    public static void main(String[] args) {
        final String topicName = System.getenv("TOPIC_NAME");
        if (topicName == null) {
            throw new RuntimeException("TOPIC_NAME environment variable must be set");
        }

        Pipeline pipeline = Pipeline.create();
        final PCollection<StarWarsMovie> movies = pipeline.apply(
            TransBeamer.newReader(
                CsvFormat.create(), "input", StarWarsMovie.class
            )
        );

        movies.apply(
            TransBeamer.newWriter(
                PubsubFormat.withTopic(topicName), StarWarsMovie.class
            )
        );

        System.out.println("\nWriting StarWarsMovies to Pubsub: " + topicName + "\n\t");
        pipeline.run().waitUntilFinish();
        System.out.println("Done!");
    }
}
