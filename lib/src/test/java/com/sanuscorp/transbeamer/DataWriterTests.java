package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link DataWriter} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The DataWriter class")
public class DataWriterTests {

    @Nested
    class when_expanded {

        // Inputs
        @Mock
        private DataFormat format;

        @Mock
        private PCollection<Person> input;

        // Interim Values
        @Mock
        private PTransform<
            @NonNull PCollection<Person>,
            @NonNull PDone
        > writer;

        @Mock
        private PDone pDone;

        private PDone result;

        @BeforeEach
        void beforeEach() {
            when(format.getWriter(Person.class)).thenReturn(writer);
            when(input.apply(anyString(), any())).thenReturn(pDone);

            result = new DataWriter<>(format, Person.class).expand(input);
        }

        @Test
        void it_gets_the_expected_writer_from_the_format() {
            verify(format).getWriter(Person.class);
        }

        @Test
        void it_applies_the_writer_to_the_input() {
            verify(input).apply(anyString(), eq(writer));
        }

        @Test
        void it_returns_the_expected_collection() {
            assertThat(result).isEqualTo(pDone);
        }
    }
}
