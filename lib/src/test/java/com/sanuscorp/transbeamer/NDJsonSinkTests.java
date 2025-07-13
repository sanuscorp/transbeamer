package com.sanuscorp.transbeamer;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sanuscorp.transbeamer.test.avro.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link NDJsonSink} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The NDJsonSink Class")
public class NDJsonSinkTests {

    // Dependencies
    private MockedConstruction<GsonBuilder> mockedGsonBuilderConstruction;

    @Mock
    private MockedStatic<Channels> mockedChannels;

    private MockedConstruction<
        OutputStreamWriter
    > mockedOutputStreamWriterConstruction;

    private List<Object> writerConstructorArgs;

    private MockedConstruction<BufferedWriter> mockedBufferedWriterConstruction;

    private List<Object> bufferedWriterConstructorArgs;

    // Inputs
    @Mock
    private WritableByteChannel channel;

    // Interim Values
    @Mock(answer = Answers.RETURNS_SELF)
    private GsonBuilder gsonBuilder;

    @Mock
    private Gson gson;

    private OutputStreamWriter writer;

    private BufferedWriter bufferedWriter;

    @Mock
    private OutputStream stream;

    private void mockOpen() {
        mockedGsonBuilderConstruction = mockConstructionWithAnswer(
            GsonBuilder.class,
            invocation -> gsonBuilder
        );
        when(gsonBuilder.create()).thenReturn(gson);

        mockedOutputStreamWriterConstruction = mockConstruction(
            OutputStreamWriter.class,
            (mock, context) -> {
                writerConstructorArgs = new ArrayList<>(context.arguments());
                writer = mock;
            }
        );
        mockedBufferedWriterConstruction = mockConstruction(
            BufferedWriter.class,
            (mock, context) -> {
                bufferedWriterConstructorArgs = new ArrayList<>(
                    context.arguments()
                );
                bufferedWriter = mock;
            }
        );

        mockedChannels.when(
            () -> Channels.newOutputStream(any(WritableByteChannel.class))
        ).thenReturn(stream);
    }

    private void afterMockOpen() {
        mockedGsonBuilderConstruction.close();
        mockedOutputStreamWriterConstruction.close();
        mockedBufferedWriterConstruction.close();
    }

    @Nested
    class when_opening_a_channel {

        @BeforeEach
        void beforeEach() {
            mockOpen();

            final NDJsonSink<Person> writer = NDJsonSink.of(Person.class);
            writer.open(channel);
        }

        @AfterEach
        void afterEach() {
            afterMockOpen();
        }

        @Test
        void it_creates_one_GsonBuilder() {
            assertThat(mockedGsonBuilderConstruction.constructed()).hasSize(1);
        }

        @Test
        void it_sets_the_formatting_style_to_compact() {
            verify(gsonBuilder).setFormattingStyle(FormattingStyle.COMPACT);
        }

        @Test
        void it_creates_the_gson_instance() {
            verify(gsonBuilder).create();
        }

        @Test
        void it_creates_one_OutputStream_from_the_channel() {
            mockedChannels.verify(() -> Channels.newOutputStream(channel));
        }

        @Test
        void it_creates_one_OutputStreamWriter() {
            assertThat(mockedOutputStreamWriterConstruction.constructed()).hasSize(1);
        }

        @Test
        void it_uses_the_stream_and_UTF_8_as_the_constructor_arguments() {
            assertThat(writerConstructorArgs).containsExactly(
                stream,
                StandardCharsets.UTF_8
            );
        }

        @Test
        void it_creates_one_BufferedWriter() {
            assertThat(mockedBufferedWriterConstruction.constructed()).hasSize(1);
        }

        @Test
        void it_uses_the_writer_as_the_BufferedWriter_constructor_argument() {
            assertThat(bufferedWriterConstructorArgs).containsExactly(writer);
        }
    }

    @Nested
    class when_opening_and_writing_an_element {

        // Inputs
        @Mock
        private Person person;

        @BeforeEach
        void beforeEach() throws IOException {
            mockOpen();

            final NDJsonSink<Person> writer = NDJsonSink.of(Person.class);
            writer.open(channel);
            writer.write(person);
        }

        @AfterEach
        void afterEach() {
            afterMockOpen();
        }

        @Test
        void it_uses_gson_to_write_the_element() {
            verify(gson).toJson(person, Person.class, bufferedWriter);
        }

        @Test
        void it_writes_a_newline_after_writing_the_element() throws IOException {
            verify(bufferedWriter).write(System.lineSeparator());
        }
    }

    @Nested
    class when_opening_and_flushing_the_writer {

        @BeforeEach
        void beforeEach() throws IOException {
            mockOpen();
            final NDJsonSink<Person> writer = NDJsonSink.of(Person.class);
            writer.open(channel);
            writer.flush();
        }

        @AfterEach
        void afterEach() {
            afterMockOpen();
        }

        @Test
        void it_flushes_the_buffered_writer() throws IOException {
            verify(bufferedWriter).flush();
        }

        @Test
        void it_closes_the_buffered_writer() throws IOException {
            verify(bufferedWriter).close();
        }
    }

    @Nested
    class when_flushing_before_opening {
        @Test
        void it_does_not_error() throws IOException {
            final NDJsonSink<Person> writer = NDJsonSink.of(Person.class);
            writer.flush();
        }
    }
}
