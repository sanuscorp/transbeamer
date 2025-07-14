package com.sanuscorp.transbeamer;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link OpenCsvReaderFn} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The OpenCsvReaderFn class")
public class OpenCsvReaderFnTests {

    // Fixtures
    private static final String FILE_NAME = "/fake/file/name.txt";

    private static final Person PERSON_1 = Person.newBuilder()
        .setFirstName("John")
        .setLastName("Doe")
        .setAge(30)
        .build();

    private static final Person PERSON_2 = Person.newBuilder()
        .setFirstName("Jane")
        .setLastName("Doe")
        .setAge(25)
        .build();

    @Nested
    class when_creating_a_par_do {

        // Dependencies
        @Mock
        private MockedStatic<ParDo> mockedParDo;

        // Interim Values
        @Mock
        private ParDo.SingleOutput<FileIO.ReadableFile, Person> parDo;

        private ParDo.SingleOutput<FileIO.ReadableFile, Person> result;

        @BeforeEach
        void beforeEach() {
            mockedParDo.when(() -> ParDo.of(any())).thenReturn(parDo);

            result = OpenCsvReaderFn.parDoOf(Person.class);
        }

        @SuppressWarnings("unchecked")
        @Test
        void it_creates_a_single_par_do() {
            mockedParDo.verify(() -> ParDo.of(any(OpenCsvReaderFn.class)));
        }

        @Test
        void it_returns_the_expected_result() {
            assertThat(result).isEqualTo(parDo);
        }
    }

    @Nested
    class when_processing_an_element {

        // Inputs
        @Mock
        private FileIO.ReadableFile file;

        @Mock
        private DoFn.OutputReceiver<Person> outputReceiver;

        // Dependencies
        @Mock
        private MockedStatic<Channels> mockedChannels;

        @SuppressWarnings("rawtypes")
        private MockedConstruction<
            CsvToBeanBuilder
        > mockedCsvToBeanBuilderConstruction;

        private List<Object> csvToBeanBuilderConstructorArgs;

        // Interim Values
        @Mock
        private MatchResult.Metadata metadata;

        @Mock
        private ResourceId resourceId;

        @Mock
        private ReadableByteChannel readableByteChannel;

        @Mock
        private Reader reader;

        @SuppressWarnings("rawtypes")
        @Mock
        private CsvToBeanBuilder csvToBeanBuilder;

        @Mock
        private CsvToBean<Person> csvToBean;

        @SuppressWarnings("unchecked")
        private void configureCsvToBeanBuilderMock(
            @SuppressWarnings("rawtypes") final CsvToBeanBuilder mock,
            final MockedConstruction.Context context
        ) {
            csvToBeanBuilderConstructorArgs = new ArrayList<>(context.arguments());
            when(mock.withType(Person.class)).thenReturn(mock);
            when(mock.build()).thenReturn(csvToBean);
            csvToBeanBuilder = mock;
        }

        @BeforeEach
        void beforeEach() throws IOException {
            when(file.getMetadata()).thenReturn(metadata);
            when(file.open()).thenReturn(readableByteChannel);
            when(metadata.resourceId()).thenReturn(resourceId);
            when(resourceId.getFilename()).thenReturn(FILE_NAME);

            mockedChannels.when(
                () -> Channels.newReader(any(), any(Charset.class))
            ).thenReturn(reader);

            mockedCsvToBeanBuilderConstruction = mockConstruction(
                CsvToBeanBuilder.class,
                this::configureCsvToBeanBuilderMock
            );

            when(csvToBean.stream()).thenReturn(Stream.of(PERSON_1, PERSON_2));

            final OpenCsvReaderFn<Person> readerFn = new OpenCsvReaderFn<>(Person.class);
            readerFn.processElement(file, outputReceiver);
        }

        @AfterEach
        void afterEach() {
            mockedCsvToBeanBuilderConstruction.close();
        }

        @Test
        void it_gets_the_file_name() {
            verify(file).getMetadata();
            verify(metadata).resourceId();
            verify(resourceId).getFilename();
        }

        @SuppressWarnings("resource")
        @Test
        void it_opens_the_file_once() throws IOException {
            verify(file).open();
        }

        @Test
        void it_creates_one_new_Reader_from_the_channel() {
            mockedChannels.verify(
                () -> Channels.newReader(
                    readableByteChannel,
                    StandardCharsets.UTF_8
                )
            );
        }

        @Test
        void it_creates_one_CsvToBeanBuilder() {
            assertThat(
                mockedCsvToBeanBuilderConstruction.constructed()
            ).hasSize(1);
        }

        @Test
        void it_provides_the_reader_to_the_CsvToBeanBuilder() {
            assertThat(csvToBeanBuilderConstructorArgs).containsExactly(reader);
        }

        @SuppressWarnings("unchecked")
        @Test
        void it_applies_the_type_to_the_CsvToBeanBuilder() {
            verify(csvToBeanBuilder).withType(Person.class);
        }

        @Test
        void it_builds_the_CsvToBean() {
            verify(csvToBeanBuilder).build();
        }

        @Test
        void it_gets_the_stream_of_elements() {
            verify(csvToBean).stream();
        }

        @Test
        void it_outputs_two_elements() {
            verify(outputReceiver, times(2)).output(any());
        }

        @Test
        void it_outputs_the_expected_elements() {
            verify(outputReceiver).output(PERSON_1);
            verify(outputReceiver).output(PERSON_2);
        }
    }
}
