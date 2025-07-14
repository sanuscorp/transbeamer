package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.io.parquet.ParquetIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the {@link ParquetSink} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The ParquetSink class")
public class ParquetSinkTests {

    // Inputs
    @Mock
    private WritableByteChannel channel;

    // Dependencies
    @Mock
    private MockedStatic<ParquetIO> mockedParquetIO;

    // Interim values
    @Mock
    private ParquetIO.Sink parquetIOSink;

    @Mock
    private Person person;

    @Nested
    class when_created {

        private ParquetSink<Person> result;

        @BeforeEach
        void beforeEach() {
            mockedParquetIO.when(() -> ParquetIO.sink(any()))
                .thenReturn(parquetIOSink);
            result = ParquetSink.of(Person.class);
        }

        @Test
        void it_creates_one_ParquetIO_sink() {
            mockedParquetIO.verify(() -> ParquetIO.sink(Person.SCHEMA$));
        }

        @Test
        void it_returns_the_expected_type() {
            assertThat(result).isInstanceOf(ParquetSink.class);
        }
    }

    @Nested
    class when_opening_writing_and_flushing {

        @BeforeEach
        void beforeEach() throws IOException {
            mockedParquetIO.when(() -> ParquetIO.sink(any()))
                .thenReturn(parquetIOSink);

            final ParquetSink<Person> parquetSink = ParquetSink.of(
                Person.class
            );
            parquetSink.open(channel);
            parquetSink.write(person);
            parquetSink.flush();
        }

        @Test
        void it_delegates_opening_to_the_parquetIOSink() throws IOException {
            verify(parquetIOSink).open(channel);
        }

        @Test
        void it_delegates_writing_to_the_parquetIOSink() throws IOException {
            verify(parquetIOSink).write(person);
        }

        @Test
        void it_delegates_flushing_to_the_parquetIOSink() throws IOException {
            verify(parquetIOSink).flush();
        }
    }
}
