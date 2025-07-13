package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
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
import static org.mockito.Mockito.verify;

/**
 * JUnit tests for the {@link NDJsonReaderFn} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The NDJsonReaderFn class")
public class NDJsonReaderFnTests {

    // Fixtures
    private static final String TEST_ELEMENT =
        "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"age\":30}";

    private static final Person EXPECTED_PERSON = Person.newBuilder()
        .setFirstName("John")
        .setLastName("Doe")
        .setAge(30)
        .build();

    @Nested
    class when_creating_a_par_do {

        // Dependencies
        @Mock
        private MockedStatic<ParDo> mockedParDo;

        // Interim values
        @Mock
        private ParDo.SingleOutput<String, Person> parDoSingleOutput;

        private ParDo.SingleOutput<String, Person> result;

        @BeforeEach
        void beforeEach() {
            mockedParDo.when(() -> ParDo.of(any()))
                .thenReturn(parDoSingleOutput);

            result = NDJsonReaderFn.parDoOf(Person.class);
        }

        @Test
        void it_creates_a_single_par_do() {
            mockedParDo.verify(() -> ParDo.of(any()));
        }

        @Test
        void it_returns_the_expected_result() {
            assertThat(result).isEqualTo(parDoSingleOutput);
        }
    }

    @Nested
    class when_processing_an_element {

        // Inputs
        @Mock
        private DoFn.OutputReceiver<Person> outputReceiver;

        @BeforeEach
        void beforeEach() {
            final NDJsonReaderFn<Person> readerFn = new NDJsonReaderFn<>(Person.class);
            readerFn.processElement(TEST_ELEMENT, outputReceiver);
        }

        @Test
        void it_outputs_one_Person_element() {
            verify(outputReceiver).output(EXPECTED_PERSON);
        }
    }
}
