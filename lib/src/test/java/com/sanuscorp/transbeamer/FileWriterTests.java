package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for the FileWriter class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The FileWriter Class")
class FileWriterTests {

    // Fixtures
    private static final String OUTPUT_LOCATION = "output/location";

    private static final Class<Person> CLASS = Person.class;

    private static final String FORMAT_SUFFIX = "formatSuffix";

    private static final String FILE_PREFIX = "testPrefix";

    private static final int NUM_SHARDS = 10;

    // Dependencies & Inputs
    @Mock
    private FileFormat format;

    @Mock
    private MockedStatic<FileIO> mockedFileIO;

    @Mock
    private MockedStatic<PDone> mockedPDone;

    @Mock
    private PCollection<Person> people;

    // Interim Values
    @Mock(answer = Answers.RETURNS_SELF)
    private FileIO.Write<Void, Person> fileIOWrite;

    @Mock
    private FileIO.Sink<Person> fileIOSink;

    @Mock
    private Pipeline pipeline;

    @Mock
    private PDone pDone;

    private PDone result;

    private void mockExpanding() {
        when(format.getSuffix()).thenReturn(FORMAT_SUFFIX);
        mockedFileIO.when(FileIO::write).thenReturn(fileIOWrite);
        when(format.getSink(any())).thenAnswer(invocation -> fileIOSink);
        when(people.getPipeline()).thenReturn(pipeline);
        mockedPDone.when(() -> PDone.in(any())).thenReturn(pDone);
    }

    @Nested
    class when_expanding {
        @BeforeEach
        void beforeEach() {
            mockExpanding();

            result = new FileWriter<>(format, OUTPUT_LOCATION, CLASS)
                .withFilePrefix(FILE_PREFIX)
                .withNumShards(NUM_SHARDS)
                .expand(people);
        }

        @Test
        void it_gets_the_format_suffix() {
            verify(format).getSuffix();
        }

        @Test
        void it_creates_one_FileIO_write() {
            mockedFileIO.verify(FileIO::write);
        }

        @Test
        void it_sets_the_output_location_on_the_FileIO_write() {
            verify(fileIOWrite).to(OUTPUT_LOCATION);
        }

        @Test
        void it_sets_the_prefix_on_the_FileIO_write() {
            verify(fileIOWrite).withPrefix(FILE_PREFIX);
        }

        @Test
        void it_sets_the_suffix_on_the_FileIO_write() {
            verify(fileIOWrite).withSuffix("." + FORMAT_SUFFIX);
        }

        @Test
        void it_sets_the_num_shards_on_the_FileIO_write() {
            verify(fileIOWrite).withNumShards(NUM_SHARDS);
        }

        @Test
        void it_creates_one_FileIO_sink() {
            verify(format).getSink(CLASS);
        }

        @Test
        void it_sets_the_sink_on_the_FileIO_write() {
            verify(fileIOWrite).via(fileIOSink);
        }

        @Test
        void it_applies_the_FileIO_write_to_the_input() {
            verify(people).apply(anyString(), eq(fileIOWrite));
        }

        @Test
        void it_gets_the_pipeline_from_the_input() {
            verify(people).getPipeline();
        }

        @Test
        void it_creates_one_PDone() {
            mockedPDone.verify(() -> PDone.in(pipeline));
        }

        @Test
        void it_returns_the_expected_pDone() {
            assertThat(result).isEqualTo(pDone);
        }
    }

    @Nested
    class when_expanding_without_setting_shards {

        @BeforeEach
        void beforeEach() {
            mockExpanding();

            result = new FileWriter<>(
                format,
                OUTPUT_LOCATION,
                CLASS
            ).expand(people);
        }

        @Test
        void it_does_not_set_the_num_shards_on_the_FileIO_write() {
            verify(fileIOWrite, never()).withNumShards(anyInt());
        }

        @Test
        void it_returns_the_expected_pDone() {
            assertThat(result).isEqualTo(pDone);
        }
    }

}
