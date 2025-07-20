# TransBeamer

[![Maven Central](https://img.shields.io/maven-central/v/com.sanuscorp/transbeamer.svg)](https://central.sonatype.com/artifact/com.sanuscorp/transbeamer)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

The TransBeamer library provides utilities for reading and writing data of various 
formats in Apache Beam pipelines, populating Avro-based PCollections as interim 
values.

The goal of the library is to make it easy for Beam pipelines to read in any 
text-based format into a `PCollection` backed by elements described by Avro
schema.  Then, when the pipeline is done processing data, make it easy to write
that data back out to a variety of formats.

## Features

- **Multiple Format Support**: Read and write CSV, Avro, Parquet, and NDJson formats
- **Consistent Reading/Writing API**: One API for multiple formats
- **Extensible Format Support**: Write your own formats as needed
- **Avro-Centric**: Uses Avro as the intermediate data format for strong schema, coder support

## Why?

Managing data as it flows through a Beam pipeline is a chore.  This is especially
true when you end up with custom POJOs intermixed with other types.  The trade-offs
between different DTO designs are not clear.  This library exists to make one
potential solution easy to implement: use Avro for every DTO.

Describing the objects within your pipeline with [Avro Schema](https://avro.apache.org/docs/++version++/getting-started-java/#defining-a-schema)
has a lot of benefits: broad tool support, strong typing support, builder & 
immutability pattern support, and many others.  It serves as a good common 
denominator in a larger, stratified data format world.

## Installation

### Maven

```xml
<dependency>
    <groupId>com.sanuscorp</groupId>
    <artifactId>transbeamer</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.sanuscorp:transbeamer:2.0.0'
```

## Quick Start

### Out-of-the-Box Examples

To run live examples, clone this repository ...

```shell
git clone https://github.com/sanuscorp/transbeamer
```

and run:

```shell
cd transbeamer
./gradlew welcome

... to see details on running the included examples.
```

## The High-Level API

The `com.sansuscorp.transbeamer.TransBeamer` class contains static methods for
creating reader (`.newReader(...)`) and writer (`.newWriter(...)` `PTransform`
instances.

In all cases, the first argument to provide is a `FileFormat` or `DataFormat`
implementation to specify the format to read or write from.  Use static methods 
on the provided implementations to specify the details of the format to use:

| Data Format      |  Format Class   | Description                                       |
|------------------|:---------------:|---------------------------------------------------|
| ✅ **CSV**        |   `CsvFormat`   | Comma-separated values                            |
| ✅ **Avro**       |  `AvroFormat`   | Apache Avro binary format                         |
| ✅ **Parquet**    | `ParquetFormat` | Columnar storage format                           |
| ✅ **NDJson**     | `NDJsonFormat`  | Newline-delimited JSON                            |
| ✅ **GCP Pubsub** | `PubsubFormat`  | Google Cloud Platform Pubsub Topics/Subscriptions |

When reading or writing FileIO-based formats (e.g., CSV, Avro, Parquet, etc),
the second argument is the location to read or write the files from.  When
reading or writing other formats (e.g., Pubsub), the location is specified in 
the format itself.  The final argument is the Avro-generated `Class` instance
that will be used in the related `PCollection`.

The API is easy to demonstrate in a few examples.

### Example: Reading CSV Data

Describe your data as an Avro schema.  For instance:

```json
{
  "namespace": "com.sanuscorp.transbeamer.samples.avro",
  "type": "record",
  "name": "StarWarsMovie",
  "fields": [
    {"name": "year", "type": "int"},
    {"name": "title", "type": "string"},
    {"name": "rating", "type": "double"}
  ],
  "javaAnnotation": [
    "org.apache.beam.sdk.schemas.annotations.DefaultSchema(org.apache.beam.sdk.extensions.avro.schemas.AvroRecordSchema.class)",
    "org.apache.beam.sdk.coders.DefaultCoder(org.apache.beam.sdk.extensions.avro.coders.AvroCoder.class)"
  ]
}
```

Use `TransBeamer` to create a new reader configured to read CSV from the local
`"input"` directory with a file prefix of `"starwars"`:

```java
import com.sanuscorp.transbeamer.CsvFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.values.PCollection;

// ...

    Pipeline pipeline = Pipeline.create();
    PCollection<StarWarsMovie> movies = pipeline.apply(
        TransBeamer.newReader(
            CsvFormat.create(),
            "input",
            StarWarsMovie.class
        ).withFilePrefix("starwars")
    );
```

To read data in other formats, use one of the other `DataFormat` implementations
(i.e. `Parquet.create()`) when creating the reader.

### Example: Writing Parquet Data

Assuming you already have a `PCollection` backed by Avro objects, writing them
out is a straight-forward affair:

```java
// ...
    PCollection<StarWarsMovie> movies = /* created elsewhere */;
    
    movies.apply(
        TransBeamer.newWriter(
            ParquetFormat.create(),
            "build",
            StarWarsMovie.class
        )
            .withNumShards(1)
            .withFilePrefix("StarWars")
    );
```

### Example: Writing CSV Data to GCP Pubsub

TransBeamer can also transform your data to and from Pubsub.  Create a Topic
in your GCP Project, and then ...

```java
    // Read in CSV files
    Pipeline pipeline = Pipeline.create();
    final PCollection<StarWarsMovie> movies = pipeline.apply(
        TransBeamer.newReader(
            CsvFormat.create(), "input", StarWarsMovie.class
        )
    );
    
    // Write each entry as a Pubsub message
    movies.apply(
        TransBeamer.newWriter(
            PubsubFormat.withTopic("/projects/my-project/topics/my-topic-name"), 
            StarWarsMovie.class
        )
    );
```

## Requirements

- Java >= 11
- Apache Beam >= 2.63
- Apache Avro >= 1.11.X

## Building from Source

```bash
git clone https://github.com/sanuscorp/transbeamer.git
cd transbeamer
./gradlew build
```

## Running Lint & Tests

```bash
./gradlew build
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License—see the [LICENSE](LICENSE) file for details.

## Support

For questions, issues, or contributions, please:

- Open an issue on [GitHub Issues](https://github.com/sanuscorp/transbeamer/issues)

## About Sanus Software & Services

TransBeamer is developed and maintained by [Sanus Software & Services](https://sanuscorp.com).