package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName( "The DataReader Class")
public class DataReaderTests {

    // Fixtures
    private static final String INPUT_LOCATION = "input/location";

    private static final Class<Person> CLASS = Person.class;

    private static final String FILE_PREFIX = "testPrefix";

    private static final String FILE_SUFFIX = "formatSuffix";

    private static final String EXPECTED_FULL_INPUT_PATTERN = INPUT_LOCATION
        + "/"
        + FILE_PREFIX
        + "*."
        + FILE_SUFFIX;

    // Inputs
    @Mock
    private DataFormat format;

    @Mock
    private PBegin pBegin;

    // Interim Values
    @Mock
    private PTransform<
        @NonNull PBegin,
        @NonNull PCollection<Person>
    > reader;

    @Mock
    private PCollection<Person> people;

    @Nested
    class when_expanding_a_reader {

        private PCollection<Person> result;

        @BeforeEach
        void beforeEach() {
            when(format.getSuffix()).thenReturn(FILE_SUFFIX);
            when(format.getReader(anyString(), any()))
                .thenAnswer(invocation -> reader);
            when(pBegin.apply(anyString(), any())).thenReturn(people);

            final DataReader<Person> reader = new DataReader<>(
                format,
                INPUT_LOCATION,
                CLASS
            )
                .withFilePrefix(FILE_PREFIX);

            result = reader.expand(pBegin);
        }

        @Test
        void it_gets_the_expected_reader_from_the_format() {
            verify(format).getReader(EXPECTED_FULL_INPUT_PATTERN, CLASS);
        }

        @Test
        void it_applies_the_reader_to_the_input() {
            verify(pBegin).apply(anyString(), eq(reader));
        }

        @Test
        void it_returns_the_expected_collection() {
            assertThat(result).isEqualTo(people);
        }
    }

    @Nested
    class when_expanded_with_a_location_ending_in_a_slash {
        private PCollection<Person> result;

        @BeforeEach
        void beforeEach() {
            when(format.getSuffix()).thenReturn(FILE_SUFFIX);
            when(format.getReader(anyString(), any()))
                .thenAnswer(invocation -> reader);
            when(pBegin.apply(anyString(), any())).thenReturn(people);

            final DataReader<Person> reader = new DataReader<>(
                format,
                INPUT_LOCATION + "/",
                CLASS
            ).withFilePrefix(FILE_PREFIX);
            result = reader.expand(pBegin);
        }

        @Test
        void it_gets_the_expected_reader_from_the_format() {
            verify(format).getReader(EXPECTED_FULL_INPUT_PATTERN, CLASS);
        }

        @Test
        void it_returns_the_expected_collection() {
            assertThat(result).isEqualTo(people);
        }
    }
}
