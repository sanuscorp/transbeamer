package com.sanuscorp.transbeamer.samples;

/**
 * A class to provide a welcome message.
 */
public class Welcome {

    /**
     * Entry point for the Welcome Gradle task.
     */
    public static void main(String[] args) {
        System.out.println("""
Welcome!
        
The TransBeamer library provides utilities for reading and writing data in
various formats, populating Avro-based PCollections as interim values.
        
This currently supports the following formats:
  - Avro
  - Parquet
  - CSV
  - ND-JSON
  - GCP Pubsub
        
The "samples" project provides apps that produce different file formats in a
variety of ways.
        """);

        System.out.println("""
        
For Instance:
-------------------------------------------------------------
See code that reads CSV, then writes out Parquet & Avro here:
        
  samples/src/main/java/.../ConvertCsvToAvroAndParquet.java
        
To run that sample, run this Gradle task:
        
  ./gradlew convertCsvToAvroAndParquet
-------------------------------------------------------------
See code that reads CSV, then writes to a Pubsub topic, as well as code that
reads from a Pubsub topic and logs messages, see here:
 
  samples/src/main/java/.../PublishCsvToPubsub.java
  samples/src/main/java/.../LogFromPubsub.java
        
To run those samples, set the TOPIC_NAME environment variable to the topic you
want to publish/read from, then run these tasks in different terminals:
  
  TOPIC_NAME=/projects/myproject-id/topics/mytopic-name ./gradlew logFromPubsub
  TOPIC_NAME=/projects/myproject-id/topics/mytopic-name ./gradlew publishCsvToPubsub
-------------------------------------------------------------
""");
    }
}
