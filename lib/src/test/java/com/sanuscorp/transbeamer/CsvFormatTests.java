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
 * Unit tests for the {@link CsvFormat} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The CsvFormat Class")
public class CsvFormatTests {

    @Nested
    class when_created {

        private CsvFormat result;

        @BeforeEach
        void beforeEach() {
            result = CsvFormat.create();
        }

        @Test
        void it_returns_the_expected_name() {
            assertThat(result.getName()).isEqualTo("CSV");
        }

        @Test
        void it_returns_the_expected_suffix() {
            assertThat(result.getSuffix()).isEqualTo("csv");
        }
    }

    @Nested
    class when_getting_the_reader {

        // Fixtures
        private static final String TEST_PATTERN = "test";

        // Dependencies
        @SuppressWarnings("rawtypes")
        @Mock
        private MockedStatic<OpenCsvReader> mockedOpenCsvReader;

        // Interim values
        @Mock
        private OpenCsvReader<Person> openCsvReader;

        private PTransform<
            @NonNull PBegin,
            @NonNull PCollection<Person>
        > result;

        @BeforeEach
        void beforeEach() {
            mockedOpenCsvReader.when(() -> OpenCsvReader.read(anyString(), any()))
                .thenReturn(openCsvReader);

            result = CsvFormat.create().getReader(TEST_PATTERN, Person.class);
        }

        @Test
        void it_creates_one_reader() {
            mockedOpenCsvReader.verify(() -> OpenCsvReader.read(TEST_PATTERN, Person.class));
        }
    }

    @Nested
    class when_getting_the_writer {

        // Dependencies
        @SuppressWarnings("rawtypes")
        @Mock
        private MockedStatic<OpenCsvSink> mockedOpenCsvSink;

        @Mock
        private OpenCsvSink<Person> openCsvSink;

        private FileIO.Sink<Person> result;

        @BeforeEach
        void beforeEach() {
            mockedOpenCsvSink.when(() -> OpenCsvSink.of(any()))
                .thenReturn(openCsvSink);

            result = CsvFormat.create().getWriter(Person.class);
        }

        @Test
        void it_creates_one_writer() {
            mockedOpenCsvSink.verify(() -> OpenCsvSink.of(Person.class));
        }
    }

}
