package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.extensions.avro.io.AvroIO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

/**
 * Unit tests for the AvroFormat class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The AvroFormat Class")
public class AvroFormatTests {

    // Fixtures
    private static final String TEST_PATTERN = "test";

    // Dependencies
    @SuppressWarnings("rawtypes")
    @Mock
    private MockedStatic<AvroReader> mockedAvroReader;

    @SuppressWarnings("rawtypes")
    @Mock
    private MockedStatic<AvroIO> mockedAvroIO;

    // Interim Values
    @Mock
    private AvroReader<Person> avroReader;

    @Mock
    private AvroIO.Sink<Person> avroIOSink;

    @Nested
    class when_getting_the_reader {

        private PTransform<
            @NonNull PBegin,
            @NonNull PCollection<Person>
        > result;

        @BeforeEach
        void beforeEach() {
            mockedAvroReader.when(() -> AvroReader.read(any(), any()))
                .thenReturn(avroReader);

            result = AvroFormat.create().getReader(TEST_PATTERN, Person.class);
        }

        @Test
        void it_creates_one_reader() {
            mockedAvroReader.verify(() -> AvroReader.read(TEST_PATTERN, Person.class));
        }

        @Test
        void it_returns_the_reader() {
            assertThat(result).isEqualTo(avroReader);
        }
    }

    @Nested
    class when_getting_the_writer {

        private FileIO.Sink<Person> result;

        @BeforeEach
        void beforeEach() {
            mockedAvroIO.when(() -> AvroIO.sink(Person.class))
                .thenReturn(avroIOSink);

            result = AvroFormat.create().getSink(Person.class);
        }

        @Test
        void it_creates_one_writer() {
            mockedAvroIO.verify(() -> AvroIO.sink(Person.class));
        }

        @Test
        void it_returns_the_writer() {
            assertThat(result).isEqualTo(avroIOSink);
        }

    }
}
