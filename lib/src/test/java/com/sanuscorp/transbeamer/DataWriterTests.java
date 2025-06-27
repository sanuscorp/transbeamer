package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
 * Unit tests for the DataWriter class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The DataWriter Class")
class DataWriterTests {

    // Fixtures
    private static final String OUTPUT_LOCATION = "output/location";

    private static final Class<Person> CLASS = Person.class;

    private static final String FORMAT_SUFFIX = "formatSuffix";

    private static final String FILE_PREFIX = "testPrefix";

    private static final int NUM_SHARDS = 10;

    // Dependencies & Inputs
    @Mock
    private DataFormat format;

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

    @BeforeEach
    void beforeEach() {
        when(format.getSuffix()).thenReturn(FORMAT_SUFFIX);

        mockedFileIO.when(FileIO::write).thenReturn(fileIOWrite);

        when(format.getWriter(any())).thenAnswer(invocation -> fileIOSink);

        when(people.getPipeline()).thenReturn(pipeline);

        mockedPDone.when(() -> PDone.in(any())).thenReturn(pDone);

        result = new DataWriter<>(format, OUTPUT_LOCATION, CLASS)
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
        verify(format).getWriter(CLASS);
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
