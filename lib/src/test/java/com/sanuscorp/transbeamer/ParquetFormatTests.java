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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * JUnit tests for the {@link ParquetFormat} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The ParquetFormat Class")
public class ParquetFormatTests {

    // Dependencies
    @SuppressWarnings("rawtypes")
    @Mock
    private MockedStatic<ParquetReader> mockedParquetReader;

    @SuppressWarnings("rawtypes")
    @Mock
    private MockedStatic<ParquetSink> mockedParquetSink;

    // Interim Values
    @Mock
    private ParquetReader<Person> parquetReader;

    @Mock
    private ParquetSink<Person> parquetSink;

    @Nested
    class when_created {
        private ParquetFormat result;

        @BeforeEach
        void beforeEach() {
            result = ParquetFormat.create();
        }

        @Test
        void it_returns_the_expected_name() {
            assertThat(result.getName()).isEqualTo("Parquet");
        }

        @Test
        void it_returns_the_expected_suffix() {
            assertThat(result.getSuffix()).isEqualTo("parquet");
        }
    }

    @Nested
    class when_getting_a_reader {

        public static final String PATTERN = "fake/pattern";
        private PTransform<
            @NonNull PBegin,
            @NonNull PCollection<Person>
        > result;

        @BeforeEach
        void beforeEach() {
            mockedParquetReader.when(
                () -> ParquetReader.read(anyString(), any())
            ).thenReturn(parquetReader);

            final ParquetFormat format = ParquetFormat.create();
            result = format.getReader(PATTERN, Person.class);
        }

        @Test
        void it_creates_one_reader() {
            mockedParquetReader.verify(
                () -> ParquetReader.read(PATTERN, Person.class)
            );
        }

        @Test
        void it_returns_the_expected_reader() {
            assertThat(result).isEqualTo(parquetReader);
        }
    }

    @Nested
    class when_getting_a_writer {

        private FileIO.Sink<Person> result;

        @BeforeEach
        void beforeEach() {
            mockedParquetSink.when(
                () -> ParquetSink.of(any())
            ).thenReturn(parquetSink);

            final ParquetFormat format = ParquetFormat.create();
            result = format.getSink(Person.class);
        }

        @Test
        void it_creates_one_writer() {
            mockedParquetSink.verify(
                () -> ParquetSink.of(any())
            );
        }

        @Test
        void it_returns_the_expected_writer() {
            assertThat(result).isEqualTo(parquetSink);
        }
    }
}
