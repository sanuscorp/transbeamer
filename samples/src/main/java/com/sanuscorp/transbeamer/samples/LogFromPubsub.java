package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.PubsubFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

public class LogFromPubsub {

    public static void main(String[] args) {
        final String topicName = System.getenv("TOPIC_NAME");
        if (topicName == null) {
            throw new RuntimeException("TOPIC_NAME environment variable must be set");
        }

        Pipeline pipeline = Pipeline.create();

        System.out.println("Listening for messages on topic: \n\t" + topicName);

        pipeline.apply(
            TransBeamer.newReader(
                PubsubFormat.withTopic(topicName),
                StarWarsMovie.class
            )
        ).apply(
            ParDo.of(new DoFn<StarWarsMovie, Void>() {
                @ProcessElement
                public void processElement(@Element StarWarsMovie movie) {
                    System.out.println(
                        "Read StarWarsMovie from Pubsub: " + movie.toString()
                    );
                }
            })
        );

        System.out.println("\nCTRL-C to stop\n");

        pipeline.run().waitUntilFinish();
    }
}
