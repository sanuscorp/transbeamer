package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.extensions.avro.io.AvroIO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the {@link AvroReader} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The AvroReader Class")
public class AvroReaderTests {

    // Fixtures
    private static final String TEST_PATTERN = "test-pattern";

    // Dependencies
    @Mock
    private MockedStatic<ReflectUtils> mockedReflectUtils;

    @Mock
    private MockedStatic<AvroIO> mockedAvroIO;

    @Mock
    private MockedStatic<ConvertGenericToSpecificFn> mockedConvertGenericToSpecificFn;

    // Interim Values
    @Mock
    private Schema schema;

    @Mock(answer = Answers.RETURNS_SELF)
    private AvroIO.Read<GenericRecord> avroIORead;

    @Mock
    private PCollection<GenericRecord> genericRecordPCollection;

    @Mock
    private PCollection<Person> personPCollection;

    @Mock
    private ParDo.SingleOutput<GenericRecord, Person> parDoSingleOutput;

    @Mock
    private PBegin pBegin;

    @Nested
    class when_creating_a_reader {

        private AvroReader<Person> result;

        @BeforeEach
        void beforeEach() {
            mockedReflectUtils.when(() -> ReflectUtils.getClassSchema(Person.class))
                .thenReturn(schema);

            result = AvroReader.read(TEST_PATTERN, Person.class);
        }

        @Test
        void it_gets_the_schema_for_the_class() {
            mockedReflectUtils.verify(() -> ReflectUtils.getClassSchema(Person.class));
        }

        @Test
        void it_returns_a_reader_with_the_correct_file_pattern() {
            assertThat(result).extracting("filePattern").isEqualTo(TEST_PATTERN);
        }

        @Test
        void it_returns_a_reader_with_the_correct_class() {
            assertThat(result).extracting("clazz").isEqualTo(Person.class);
        }

        @Test
        void it_returns_a_reader_with_the_correct_schema() {
            assertThat(result).extracting("schema").isEqualTo(schema);
        }
    }

    @Nested
    class when_expanding_the_transform {

        private PCollection<Person> result;

        @BeforeEach
        void beforeEach() {
            mockedReflectUtils.when(
                () -> ReflectUtils.getClassSchema(Person.class)
            ).thenReturn(schema);

            mockedAvroIO.when(
                () -> AvroIO.readGenericRecords(any(Schema.class))
            ).thenReturn(avroIORead);

            // Use doReturn().when() syntax for more lenient argument matching
            org.mockito.Mockito.doReturn(genericRecordPCollection)
                .when(pBegin).apply(any(), any());

            mockedConvertGenericToSpecificFn.when(
                () -> ConvertGenericToSpecificFn.parDoOf(Person.class)
            ).thenReturn(parDoSingleOutput);

            // Use doReturn().when() syntax for more lenient argument matching
            org.mockito.Mockito.doReturn(personPCollection)
                .when(genericRecordPCollection).apply(any(), any());

            final AvroReader<Person> avroReader =
                AvroReader.read(TEST_PATTERN, Person.class);
            result = avroReader.expand(pBegin);
        }

        @Test
        void it_creates_an_avro_io_reader_with_the_schema() {
            mockedAvroIO.verify(() -> AvroIO.readGenericRecords(schema));
        }

        @Test
        void it_applies_the_avro_io_reader_to_the_input() {
            verify(pBegin).apply(
                "Read Avro from " + TEST_PATTERN,
                avroIORead
            );
        }

        @Test
        void it_creates_a_converter_for_the_class() {
            mockedConvertGenericToSpecificFn.verify(
                () -> ConvertGenericToSpecificFn.parDoOf(Person.class)
            );
        }

        @Test
        void it_applies_the_converter_to_the_generic_records() {
            verify(genericRecordPCollection).apply(
                "Convert to " + Person.class.getName(),
                parDoSingleOutput
            );
        }

        @Test
        void it_returns_the_converted_collection() {
            assertThat(result).isEqualTo(personPCollection);
        }
    }
}
