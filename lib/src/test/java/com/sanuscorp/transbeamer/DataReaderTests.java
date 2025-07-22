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

/**
 * Unit tests for the {@link DataReader} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The DataReader class")
public class DataReaderTests {

    @Nested
    class when_expanded {

        // Inputs
        @Mock
        private DataFormat format;

        @Mock
        private PBegin input;

        // Interim Values
        @Mock
        private PTransform<
            @NonNull PBegin,
            @NonNull PCollection<Person>
        > reader;

        @Mock
        private PCollection<Person> people;

        private PCollection<Person> result;

        @BeforeEach
        void beforeEach() {
            when(format.getReader(Person.class)).thenReturn(reader);
            when(input.apply(anyString(), any())).thenReturn(people);
            result = new DataReader<>(format, Person.class).expand(input);
        }

        @Test
        void it_gets_the_expected_reader_from_the_format() {
            verify(format).getReader(Person.class);
        }

        @Test
        void it_applies_the_reader_to_the_input() {
            verify(input).apply(anyString(), eq(reader));
        }

        @Test
        void it_returns_the_expected_collection() {
            assertThat(result).isEqualTo(people);
        }
    }
}
