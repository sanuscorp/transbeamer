package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockConstruction;

/**
 * Unit tests for the {@link TransBeamer} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The TransBeamer Class")
public class TransBeamerTests {

    // Fixtures
    private static final String LOCATION = "test/location";

    private static final FileFormat FILE_FORMAT = CsvFormat.create();

    private static final DataFormat DATA_FORMAT = PubsubFormat.withTopic(
        "fake-topic"
    );

    @Nested
    class when_getting_a_new_file_reader {

        // Dependencies
        @SuppressWarnings("rawtypes")
        private MockedConstruction<FileReader> mockedFileReaderConstruction;

        // Interim values
        @Mock
        private FileReader<Person> fileReader;

        private List<Object> fileReaderConstructionArgs;

        private FileReader<Person> result;

        @SuppressWarnings("unchecked")
        @BeforeEach
        void beforeEach() {
            mockedFileReaderConstruction = mockConstruction(
                FileReader.class,
                (mock, context) -> {
                    fileReaderConstructionArgs = new ArrayList<>(context.arguments());
                    fileReader = mock;
                }
            );

            result = TransBeamer.newReader(
                FILE_FORMAT,
                LOCATION,
                Person.class
            );
        }

        @AfterEach
        void afterEach() {
            mockedFileReaderConstruction.close();
        }

        @Test
        void it_creates_one_file_reader() {
            assertThat(mockedFileReaderConstruction.constructed()).hasSize(1);
        }

        @Test
        void it_provides_the_expected_args_to_the_file_reader() {
            assertThat(fileReaderConstructionArgs).containsExactly(
                FILE_FORMAT,
                LOCATION,
                Person.class
            );
        }

        @Test
        void it_returns_the_constructed_reader() {
            assertThat(result).isEqualTo(fileReader);
        }
    }

    @Nested
    class when_getting_a_new_data_reader {
        // Dependencies
        @SuppressWarnings("rawtypes")
        private MockedConstruction<DataReader> mockedDataReaderConstruction;

        // Interim values
        @Mock
        private DataReader<Person> dataReader;

        private List<Object> dataReaderConstructionArgs;

        private DataReader<Person> result;

        @SuppressWarnings("unchecked")
        @BeforeEach
        void beforeEach() {
            mockedDataReaderConstruction = mockConstruction(
                DataReader.class,
                (mock, context) -> {
                    dataReaderConstructionArgs = new ArrayList<>(context.arguments());
                    dataReader = mock;
                }
            );

            result = TransBeamer.newReader(
                DATA_FORMAT,
                Person.class
            );
        }

        @AfterEach
        void afterEach() {
            mockedDataReaderConstruction.close();
        }

        @Test
        void it_creates_one_file_reader() {
            assertThat(mockedDataReaderConstruction.constructed()).hasSize(1);
        }

        @Test
        void it_provides_the_expected_args_to_the_file_reader() {
            assertThat(dataReaderConstructionArgs).containsExactly(
                DATA_FORMAT,
                Person.class
            );
        }

        @Test
        void it_returns_the_constructed_reader() {
            assertThat(result).isEqualTo(dataReader);
        }
    }

    @Nested
    class when_getting_a_new_file_writer {
        // Dependencies
        @SuppressWarnings("rawtypes")
        private MockedConstruction<FileWriter> mockedFileWriterConstruction;

        // Interim values
        @Mock
        private FileWriter<Person> fileWriter;

        private List<Object> fileWriterConstructionArgs;

        private FileWriter<Person> result;

        @SuppressWarnings("unchecked")
        @BeforeEach
        void beforeEach() {
            mockedFileWriterConstruction = mockConstruction(
                FileWriter.class,
                (mock, context) -> {
                    fileWriterConstructionArgs = new ArrayList<>(context.arguments());
                    fileWriter = mock;
                }
            );

            result = TransBeamer.newWriter(
                FILE_FORMAT,
                LOCATION,
                Person.class
            );
        }

        @AfterEach
        void afterEach() {
            mockedFileWriterConstruction.close();
        }

        @Test
        void it_creates_one_file_writer() {
            assertThat(mockedFileWriterConstruction.constructed()).hasSize(1);
        }

        @Test
        void it_provides_the_expected_args_to_the_file_writer() {
            assertThat(fileWriterConstructionArgs).containsExactly(
                FILE_FORMAT,
                LOCATION,
                Person.class
            );
        }

        @Test
        void it_returns_the_constructed_writer() {
            assertThat(result).isEqualTo(fileWriter);
        }
    }

    @Nested
    class when_getting_a_new_data_writer {
        // Dependencies
        @SuppressWarnings("rawtypes")
        private MockedConstruction<DataWriter> mockedDataWriterConstruction;

        // Interim values
        @Mock
        private DataWriter<Person> dataWriter;

        private List<Object> dataWriterConstructionArgs;

        private DataWriter<Person> result;

        @SuppressWarnings("unchecked")
        @BeforeEach
        void beforeEach() {
            mockedDataWriterConstruction = mockConstruction(
                DataWriter.class,
                (mock, context) -> {
                    dataWriterConstructionArgs = new ArrayList<>(context.arguments());
                    dataWriter = mock;
                }
            );

            result = TransBeamer.newWriter(
                DATA_FORMAT,
                Person.class
            );
        }

        @AfterEach
        void afterEach() {
            mockedDataWriterConstruction.close();
        }

        @Test
        void it_creates_one_file_writer() {
            assertThat(mockedDataWriterConstruction.constructed()).hasSize(1);
        }

        @Test
        void it_provides_the_expected_args_to_the_file_writer() {
            assertThat(dataWriterConstructionArgs).containsExactly(
                DATA_FORMAT,
                Person.class
            );
        }

        @Test
        void it_returns_the_constructed_writer() {
            assertThat(result).isEqualTo(dataWriter);
        }
    }
}
