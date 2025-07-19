package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.io.parquet.ParquetIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link ParquetReader} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The ParquetReader class")
public class ParquetReaderTests {

    // Fixtures
    private static final String PATTERN = "fake/file/pattern";

    @Nested
    class when_creating_a_reader {

        private ParquetReader<Person> result;

        @BeforeEach
        void beforeEach() {
            result = ParquetReader.read(PATTERN, Person.class);
        }

        @Test
        void returns_a_reader() {
            assertThat(result).isInstanceOf(ParquetReader.class);
        }
    }

    @Nested
    class when_expanding {

        // Inputs
        @Mock
        private PBegin input;

        // Dependencies
        @Mock
        private MockedStatic<ParquetIO> mockedParquetIO;

        @SuppressWarnings("rawtypes")
        @Mock
        private MockedStatic<
            ConvertGenericToSpecificFn
        > mockedConvertGenericToSpecificFn;

        // Interim Values
        @Mock(answer = Answers.RETURNS_SELF)
        private ParquetIO.Read parquetIORead;

        @Mock
        private PCollection<GenericRecord> genericRecords;

        @Mock
        private ParDo.SingleOutput<
            GenericRecord,
            Person
        > genericToSpecificParDo;

        @Mock
        private PCollection<Person> people;

        private PCollection<Person> result;

        @BeforeEach
        void beforeEach() {
            mockedParquetIO.when(() -> ParquetIO.read(any()))
                .thenReturn(parquetIORead);

            when(input.apply(anyString(), any())).thenReturn(genericRecords);

            mockedConvertGenericToSpecificFn.when(
                () -> ConvertGenericToSpecificFn.parDoOf(any())
            ).thenReturn(genericToSpecificParDo);
            when(genericRecords.apply(anyString(), any())).thenReturn(people);

            result = ParquetReader
                .read(PATTERN, Person.class)
                .expand(input);
        }

        @Test
        void it_creates_one_ParquetIO_read() {
            mockedParquetIO.verify(() -> ParquetIO.read(Person.SCHEMA$));
        }

        @Test
        void it_applies_the_pattern_to_the_ParquetIO_read() {
            verify(parquetIORead).from(PATTERN);
        }

        @Test
        void it_applies_the_ParquetIO_read_to_the_input() {
            verify(input).apply(anyString(), eq(parquetIORead));
        }

        @Test
        void it_creates_one_ConvertGenericToSpecificFn_pardo() {
            mockedConvertGenericToSpecificFn.verify(
                () -> ConvertGenericToSpecificFn.parDoOf(Person.class)
            );
        }

        @Test
        void it_applies_the_convert_pardo_to_the_genericRecords() {
            verify(genericRecords).apply(
                anyString(),
                eq(genericToSpecificParDo)
            );
        }

        @Test
        void it_returns_the_expected_collection() {
            assertThat(result).isEqualTo(people);
        }

    }

}
