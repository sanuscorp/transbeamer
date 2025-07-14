package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * Unit tests for the {@link ReflectUtils} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The ReflectUtils class")
public class ReflectUtilsTests {

    // Fixtures
    private static final ReflectiveOperationException RO_EXCEPTION =
        new ReflectiveOperationException("Not a Schema");

    @Nested
    class when_instantiating_a_person {

        private Person result;

        @BeforeEach
        void beforeEach() {
            result = ReflectUtils.instantiate(Person.class);
        }

        @Test
        void it_returns_an_instance_of_the_given_class() {
            assertThat(result).isInstanceOf(Person.class);
        }

        @Test
        void it_has_a_null_first_name() {
            assertThat(result.getFirstName()).isNull();
        }

        @Test
        void it_has_a_null_last_name() {
            assertThat(result.getLastName()).isNull();
        }

        @Test
        void it_has_a_zero_age() {
            assertThat(result.getAge()).isEqualTo(0);
        }
    }

    @Nested
    class when_attempting_to_instantiate_a_non_javabean {

        private Throwable thrown;

        @BeforeEach
        void beforeEach() {
            thrown = catchThrowableOfType(
                () -> ReflectUtils.instantiate(GenericData.Record.class),
                IllegalArgumentException.class
            );
        }

        @Test
        void it_throws_an_exception_with_the_expected_message() {
            assertThat(thrown).hasMessage(
                "Class "
                    + GenericData.Record.class.getSimpleName()
                    + " cannot be instantiated"
            );
        }

        @Test
        void it_throws_an_exception_with_the_expected_cause() {
            assertThat(thrown).hasCauseInstanceOf(
                NoSuchMethodException.class
            );
        }
    }

    @Nested
    class when_getting_the_class_schema {

        private Schema result;

        @BeforeEach
        void beforeEach() {
            result = ReflectUtils.getClassSchema(Person.class);
        }

        @Test
        void it_returns_the_expected_schema() {
            assertThat(result).isEqualTo(Person.SCHEMA$);
        }
    }

    @Nested
    class when_class_schema_errors {

        private Throwable thrown;

        @Mock
        private MockedStatic<Person> mockedPerson;

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @BeforeEach
        void beforeEach() {
            mockedPerson.when(
                Person::getClassSchema
            ).thenAnswer(
                invocation -> {
                    throw RO_EXCEPTION;
                }
            );

            thrown = catchThrowableOfType(
                () -> ReflectUtils.getClassSchema(Person.class),
                IllegalArgumentException.class
            );
        }

        @Test
        void it_throws_an_exception_with_the_expected_message() {
            assertThat(thrown).hasMessage(
                "'getClassSchema' cannot be invoked on Person"
            );
        }

        @Test
        void it_throws_an_exception_with_the_expected_root_cause() {
            assertThat(thrown).hasRootCause(RO_EXCEPTION);
        }
    }
}
