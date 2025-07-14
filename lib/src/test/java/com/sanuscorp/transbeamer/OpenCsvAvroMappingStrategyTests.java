package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for the {@link OpenCsvAvroMappingStrategy} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The OpenCsvAvroMapping Strategy class")
public class OpenCsvAvroMappingStrategyTests {

    // Inputs
    @Mock
    private Person person;

    @Nested
    class when_created {

        private OpenCsvAvroMappingStrategy<Person> result;

        @BeforeEach
        void beforeEach() {
            result = OpenCsvAvroMappingStrategy.of(Person.class);
        }

        @Test
        void it_sets_the_appropriate_type() {
            assertThat(result.getType()).isEqualTo(Person.class);
        }
    }

    @Nested
    class when_generating_the_header_for_the_Person_class {

        private String[] generatedHeader;

        private OpenCsvAvroMappingStrategy<Person> mappingStrategy;

        @BeforeEach
        void beforeEach() {
            mappingStrategy = OpenCsvAvroMappingStrategy
                .of(Person.class);
            generatedHeader = mappingStrategy.generateHeader(person);
        }

        @Test
        void it_does_not_interact_with_the_given_bean() {
            verifyNoInteractions(person);
        }

        @Test
        void it_generates_the_expected_header() {
            assertThat(generatedHeader).containsExactly(
                "age",
                "firstName",
                "lastName"
            );
        }

        @Test
        void it_can_find_the_age_header() {
            assertThat(mappingStrategy.findHeader(0)).isEqualTo("age");
        }

        @Test
        void it_can_find_the_first_name_header() {
            assertThat(mappingStrategy.findHeader(1)).isEqualTo("firstName");
        }

        @Test
        void it_can_find_the_last_name_header() {
            assertThat(mappingStrategy.findHeader(2)).isEqualTo("lastName");
        }
    }

    @Nested
    class when_generating_the_header_twice {
        private String[] firstHeader;

        private String[] secondHeader;

        @BeforeEach
        void beforeEach() {
            final OpenCsvAvroMappingStrategy<Person> mappingStrategy =
                OpenCsvAvroMappingStrategy.of(Person.class);
            firstHeader = mappingStrategy.generateHeader(person);
            secondHeader = mappingStrategy.generateHeader(person);
        }

        @Test
        void it_generates_the_same_header() {
            assertThat(firstHeader).isEqualTo(secondHeader);
        }
    }
}
