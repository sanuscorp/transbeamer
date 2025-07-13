package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.extensions.avro.coders.AvroCoder;
import org.apache.beam.sdk.io.TextIO;
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
 * Unit tests for the {@link NDJsonReader} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The NDJsonReader Class")
public class NDJsonReaderTests {

    // Fixtures
    private static final String FILE_PATTERN = "input/location";

    // Inputs
    @Mock
    private PBegin pBegin;

    // Dependencies
    @Mock
    private MockedStatic<TextIO> mockedTextIO;

    @SuppressWarnings("rawtypes")
    @Mock
    private MockedStatic<NDJsonReaderFn> mockedNDJsonReaderFn;

    // Interim results
    @Mock
    private Pipeline pipeline;

    @Mock(answer = Answers.RETURNS_SELF)
    private TextIO.Read textIORead;

    @Mock
    private PCollection<String> lines;

    @Mock
    private ParDo.SingleOutput<String, Person> ndJsonReaderParDo;

    @Mock(answer = Answers.RETURNS_SELF)
    private PCollection<Person> people;

    @Nested
    class when_expanding_a_reader {

        private PCollection<Person> result;

        @BeforeEach
        void beforeEach() {
            when(pBegin.getPipeline()).thenReturn(pipeline);
            mockedTextIO.when(TextIO::read).thenReturn(textIORead);
            when(pipeline.apply(anyString(), any())).thenReturn(lines);

            mockedNDJsonReaderFn.when(
                () -> NDJsonReaderFn.parDoOf(any())
            ).thenReturn(ndJsonReaderParDo);

            when(lines.apply(anyString(), any())).thenReturn(people);

            result = NDJsonReader
                .read(FILE_PATTERN, Person.class)
                .expand(pBegin);
        }

        @Test
        void it_gets_the_pipeline_from_the_input() {
            verify(pBegin).getPipeline();
        }

        @Test
        void it_creates_one_TextIO_read() {
            mockedTextIO.verify(TextIO::read);
        }

        @Test
        void it_reads_from_the_given_file_pattern() {
            verify(textIORead).from(FILE_PATTERN);
        }

        @Test
        void it_applies_the_TextIO_Read_to_the_pipeline() {
            verify(pipeline).apply(anyString(), eq(textIORead));
        }

        @Test
        void it_creates_one_NDJsonReaderFn_pardo() {
            mockedNDJsonReaderFn.verify(
                () -> NDJsonReaderFn.parDoOf(Person.class)
            );
        }

        @Test
        void it_applies_the_NDJsonReaderFn_pardo_to_the_lines() {
            verify(lines).apply(anyString(), eq(ndJsonReaderParDo));
        }

        @Test
        void it_sets_the_coder_on_the_collection() {
            verify(people).setCoder(AvroCoder.of(Person.class));
        }

        @Test
        void it_returns_the_expected_collection() {
            assertThat(result).isEqualTo(people);
        }
    }
}
