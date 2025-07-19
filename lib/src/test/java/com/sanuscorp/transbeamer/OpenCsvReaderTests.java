package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.extensions.avro.coders.AvroCoder;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.fs.MatchResult;
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
 * Unit tests for the {@link OpenCsvReader} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The OpenCsvReader Class")
public class OpenCsvReaderTests {

    // Fixtures
    private static final String TEST_PATTERN = "test";

    // Inputs
    @Mock
    private PBegin pBegin;

    // Dependencies
    @Mock
    private MockedStatic<FileIO> mockedFileIO;

    @SuppressWarnings("rawtypes")
    @Mock
    private MockedStatic<OpenCsvReaderFn> mockedOpenCsvReaderFn;

    @SuppressWarnings("rawtypes")
    @Mock
    private MockedStatic<AvroCoder> mockedAvroCoder;

    // Interim Values
    @Mock
    private Pipeline pipeline;

    @Mock(answer = Answers.RETURNS_SELF)
    private FileIO.Match fileIOMatch;

    @Mock
    private PCollection<MatchResult.Metadata> metadata;

    @Mock
    private FileIO.ReadMatches fileIOReadMatches;

    @Mock
    private PCollection<FileIO.ReadableFile> readableFiles;

    @Mock
    private ParDo.SingleOutput<FileIO.ReadableFile, Person> openCsvReaderParDo;

    @Mock(answer = Answers.RETURNS_SELF)
    private PCollection<Person> people;

    @Mock
    private AvroCoder<Person> avroCoder;

    @Nested
    class when_expanded {

        private PCollection<Person> result;

        @BeforeEach
        void beforeEach() {
            when(pBegin.getPipeline()).thenReturn(pipeline);

            mockedFileIO.when(FileIO::match).thenReturn(fileIOMatch);
            when(pipeline.apply(anyString(), any())).thenReturn(metadata);

            mockedFileIO.when(FileIO::readMatches)
                .thenReturn(fileIOReadMatches);
            when(metadata.apply(anyString(), any())).thenReturn(readableFiles);

            mockedOpenCsvReaderFn.when(() -> OpenCsvReaderFn.parDoOf(any()))
                .thenReturn(openCsvReaderParDo);

            when(readableFiles.apply(anyString(), any())).thenReturn(people);

            mockedAvroCoder.when(() -> AvroCoder.of(Person.class))
                .thenReturn(avroCoder);

            result = OpenCsvReader
                .read(TEST_PATTERN, Person.class)
                .expand(pBegin);
        }

        @Test
        void it_gets_the_pipeline_from_the_input() {
            verify(pBegin).getPipeline();
        }

        @Test
        void it_creates_one_FileIO_match() {
            mockedFileIO.verify(FileIO::match);
        }

        @Test
        void it_applies_the_pattern_to_the_FileIO_match() {
            verify(fileIOMatch).filepattern(TEST_PATTERN);
        }

        @Test
        void it_applies_the_FileIO_match_to_the_input() {
            verify(pipeline).apply(anyString(), eq(fileIOMatch));
        }

        @Test
        void it_creates_one_FileIO_readMatches() {
            mockedFileIO.verify(FileIO::readMatches);
        }

        @Test
        void it_applies_the_FileIO_readMatches_to_the_metadata() {
            verify(metadata).apply(anyString(), eq(fileIOReadMatches));
        }

        @Test
        void it_creates_one_OpenCsvReaderFn_pardo() {
            mockedOpenCsvReaderFn.verify(
                () -> OpenCsvReaderFn.parDoOf(Person.class)
            );
        }

        @Test
        void it_applies_the_OpenCsvReaderFn_pardo_to_the_readableFiles() {
            verify(readableFiles).apply(anyString(), eq(openCsvReaderParDo));
        }

        @Test
        void it_creates_one_AvroCoder_of_Person() {
            mockedAvroCoder.verify(() -> AvroCoder.of(Person.class));
        }

        @Test
        void it_sets_the_coder_on_the_collection() {
            verify(people).setCoder(avroCoder);
        }

        @Test
        void it_returns_the_expected_collection() {
            assertThat(result).isEqualTo(people);
        }
    }
}
