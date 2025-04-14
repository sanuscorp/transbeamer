package com.sanuscorp.transbeamer.samples;

import com.sanuscorp.transbeamer.ParquetFormat;
import com.sanuscorp.transbeamer.TransBeamer;
import com.sanuscorp.transbeamer.samples.avro.StarWarsMovie;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This application creates a Parquet file with Star Wars movie data.  The data
 * is embedded in this file for convenience.
 */
public class CreateParquet {

    /**
     * Main entry point for the CreateStarWarsParquet application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Creating StarWars.parquet file...");

        // Create the input directory if it doesn't exist
        // Create the output directory if it doesn't exist
        Path outputPath = Paths.get("build");
        if (Files.notExists(outputPath)) {
            Files.createDirectories(outputPath);
        }

        List<StarWarsMovie> movies = makeMovieList();
        Pipeline pipeline = Pipeline.create();

        // Create a PCollection from the list of movies
        PCollection<StarWarsMovie> movieCollection = pipeline.apply(
            Create.of(movies)
        );

        // Write the PCollection to a Parquet file
        movieCollection.apply(
            TransBeamer.newWriter(
                ParquetFormat.create(),
                "build",
                StarWarsMovie.class
            )
                .withNumShards(1)
                .withFilePrefix("StarWars")
        );

        // Run the pipeline
        pipeline.run().waitUntilFinish();

        System.out.println(
            "Parquet created successfully at: build/StarWars*.parquet"
        );
    }

    @NotNull
    private static List<StarWarsMovie> makeMovieList() {
        // Create a list of Star Wars movies
        List<StarWarsMovie> movies = new ArrayList<>();
        addMovie(movies, 1977, "Star Wars: Episode IV - A New Hope", 0.93F);
        addMovie(movies, 1980, "Star Wars: Episode V - The Empire Strikes Back", 0.94F);
        addMovie(movies, 1983, "Star Wars: Episode VI - Return of the Jedi", 0.82F);
        addMovie(movies, 1999, "Star Wars: Episode I - The Phantom Menace", 0.52F);
        addMovie(movies, 2002, "Star Wars: Episode II - Attack of the Clones", 0.65F);
        addMovie(movies, 2005, "Star Wars: Episode III - Revenge of the Sith", 0.80F);
        addMovie(movies, 2015, "Star Wars: Episode VII - The Force Awakens", 0.93F);
        addMovie(movies, 2017, "Star Wars: Episode VIII - The Last Jedi", 0.91F);
        addMovie(movies, 2019, "Star Wars: Episode IX - The Rise of Skywalker", 0.52F);
        return movies;
    }

    /**
     * Helper method to add a movie to the list.
     * @param movies The list of movies
     * @param year The release year
     * @param title The movie title
     */
    private static void addMovie(
        final List<StarWarsMovie> movies,
        final int year,
        final String title,
        final double rating
    ) {
        StarWarsMovie movie = StarWarsMovie.newBuilder()
            .setYear(year)
            .setTitle(title)
            .setRating(rating)
            .build();
        movies.add(movie);
    }
}
