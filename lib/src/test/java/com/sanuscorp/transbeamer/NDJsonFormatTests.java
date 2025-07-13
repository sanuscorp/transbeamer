package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Unit tests for the NDJsonFormat class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The NDJsonFormat Class")
public class NDJsonFormatTests {

    // Fixtures
    private static final String FILE_PATTERN = "fake/pattern";

    // Dependencies
    @SuppressWarnings("rawtypes")
    @Mock
    private MockedStatic<NDJsonReader> mockedNDJsonReader;

    @SuppressWarnings("rawtypes")
    @Mock
    private MockedStatic<NDJsonSink> mockedNDJsonWriter;

    // Interim values
    @Mock
    private NDJsonReader<Person> ndJsonReader;

    @Mock
    private NDJsonSink<Person> ndJsonSink;

    @Nested
    class when_created {

        private NDJsonFormat result;

        @BeforeEach
        void beforeEach() {
            result = NDJsonFormat.create();
        }

        @Test
        void it_returns_the_expected_name() {
            assertThat(result.getName()).isEqualTo("ND-JSON");
        }

        @Test
        void it_returns_the_expected_suffix() {
            assertThat(result.getSuffix()).isEqualTo("ndjson");
        }

    }

    @Nested
    class when_getting_a_reader {

        private PTransform<
            @NonNull PBegin,
            @NonNull PCollection<Person>
            > result;

        @BeforeEach
        void beforeEach() {
            mockedNDJsonReader.when(() -> NDJsonReader.read(anyString(), any()))
                .thenReturn(ndJsonReader);

            result = NDJsonFormat.create().getReader(FILE_PATTERN, Person.class);
        }

        @Test
        void getting_the_reader_creates_one_reader() {
            mockedNDJsonReader.verify(() -> NDJsonReader.read(anyString(), any()));
        }

        @Test
        void it_returns_the_expected_reader() {
            assertThat(result).isEqualTo(ndJsonReader);
        }
    }

    @Nested
    class when_getting_a_writer {

        private FileIO.Sink<Person> result;

        @BeforeEach
        void beforeEach() {
            mockedNDJsonWriter.when(() -> NDJsonSink.of(any()))
                .thenReturn(ndJsonSink);

            result = NDJsonFormat.create().getSink(Person.class);
        }

        @Test
        void getting_the_writer_creates_one_writer() {
            mockedNDJsonWriter.verify(() -> NDJsonSink.of(Person.class));
        }

        @Test
        void it_returns_the_expected_writer() {
            assertThat(result).isEqualTo(ndJsonSink);
        }
    }
}
