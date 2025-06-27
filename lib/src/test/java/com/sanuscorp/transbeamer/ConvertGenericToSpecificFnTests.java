package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the {@link ConvertGenericToSpecificFn} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The ConvertGenericToSpecificFn class")
public class ConvertGenericToSpecificFnTests {

    // Fixtures
    private static final String FIRST_NAME = "First Name";

    private static final String LAST_NAME = "Last Name";

    private static final int AGE = 10;

    private static final GenericRecord GENERIC_PERSON;

    static {
        GENERIC_PERSON = new GenericRecordBuilder(
            ReflectUtils.getClassSchema(Person.class)
        ).set("firstName", FIRST_NAME)
            .set("lastName", LAST_NAME)
            .set("age", AGE)
            .build();
    }

    private static final Person EXPECTED_PERSON = Person.newBuilder()
        .setFirstName(FIRST_NAME)
        .setLastName(LAST_NAME)
        .setAge(AGE)
        .build();

    @Nested
    class when_created_wrapped_in_a_par_do {
        // Dependencies
        @Mock
        private MockedStatic<ParDo> mockedParDo;

        // Interim Values
        @Mock
        private ParDo.SingleOutput<GenericRecord, Person> parDoSingleOutput;

        private ParDo.SingleOutput<GenericRecord, Person> result;

        @Captor
        private ArgumentCaptor<ConvertGenericToSpecificFn<Person>> captor;

        @BeforeEach
        void beforeEach() {
            mockedParDo.when(() -> ParDo.of(any()))
                .thenReturn(parDoSingleOutput);

            result = ConvertGenericToSpecificFn.parDoOf(Person.class);

            mockedParDo.verify(() -> ParDo.of(captor.capture()));
        }

        @Test
        void it_created_the_expected_converter_dofn() {
            final ConvertGenericToSpecificFn<Person> converter = captor.getValue();
            assertThat(converter).extracting("clazz").isEqualTo(Person.class);
        }

        @Test
        void it_returns_the_expected_result() {
            assertThat(result).isEqualTo(parDoSingleOutput);
        }
    }

    @Nested
    class when_processing_an_element {

        @Mock
        private DoFn.OutputReceiver<Person> outputReceiver;

        @BeforeEach
        void beforeEach() {
            final ConvertGenericToSpecificFn<Person> converter =
                new ConvertGenericToSpecificFn<>(Person.class);
            converter.processElement(GENERIC_PERSON, outputReceiver);
        }

        @Test
        void it_outputs_one_Person_element() {
            verify(outputReceiver).output(EXPECTED_PERSON);
        }
    }
}
