package com.sanuscorp.transbeamer;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.sanuscorp.transbeamer.test.avro.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link OpenCsvSink} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The OpenCsvSink class")
public class OpenCsvSinkTests {

    // Fixtures
    public static final CsvDataTypeMismatchException CSV_EXCEPTION =
        new CsvDataTypeMismatchException("Test exception");

    // Inputs
    @Mock
    private WritableByteChannel channel;

    @Mock
    private Person person;

    // Dependencies
    @Mock
    private MockedStatic<Channels> mockedChannels;

    @SuppressWarnings("rawtypes")
    @Mock
    private MockedStatic<
        OpenCsvAvroMappingStrategy
    > mockedOpenCsvAvroMappingStrategy;

    @SuppressWarnings("rawtypes")
    private MockedConstruction<
        StatefulBeanToCsvBuilder
    > mockedStatefulBeanToCsvBuilderConstruction;

    private List<Object> statefulBeanToCsvBuilderConstructorArgs;

    // Interim values
    @Mock
    private Writer writer;

    @Mock
    private OpenCsvAvroMappingStrategy<Person> strategy;

    @Mock
    private StatefulBeanToCsvBuilder<Person> statefulBeanToCsvBuilder;

    @Mock
    private StatefulBeanToCsv<Person> beanToCsv;

    @SuppressWarnings("unchecked")
    private void configureStatefulBeanToCsvBuilder(
        @SuppressWarnings("rawtypes") final StatefulBeanToCsvBuilder mock,
        final MockedConstruction.Context context
    ) {
        statefulBeanToCsvBuilderConstructorArgs = new ArrayList<>(
            context.arguments()
        );
        when(mock.withMappingStrategy(any())).thenReturn(mock);
        when(mock.withOrderedResults(anyBoolean())).thenReturn(mock);
        when(mock.build()).thenReturn(beanToCsv);
        statefulBeanToCsvBuilder = mock;
    }

    private void mockOpen() {
        mockedChannels.when(() -> Channels.newWriter(channel, StandardCharsets.UTF_8))
            .thenReturn(writer);
        mockedOpenCsvAvroMappingStrategy.when(
            () -> OpenCsvAvroMappingStrategy.of(any())
        ).thenReturn(strategy);

        mockedStatefulBeanToCsvBuilderConstruction = mockConstruction(
            StatefulBeanToCsvBuilder.class,
            this::configureStatefulBeanToCsvBuilder
        );
    }

    @Nested
    class when_open_and_write_an_element_and_flushed {
        @BeforeEach
        void beforeEach() throws IOException {
            mockOpen();

            final OpenCsvSink<Person> sink = OpenCsvSink.of(Person.class);
            sink.open(channel);
            sink.write(person);
            sink.flush();
        }

        @AfterEach
        void afterEach() {
            mockedStatefulBeanToCsvBuilderConstruction.close();
        }

        @Test
        void it_creates_one_new_writer() {
            mockedChannels.verify(
                () -> Channels.newWriter(channel, StandardCharsets.UTF_8)
            );
        }

        @Test
        void it_creates_one_new_mapping_strategy() {
            mockedOpenCsvAvroMappingStrategy.verify(
                () -> OpenCsvAvroMappingStrategy.of(Person.class)
            );
        }

        @Test
        void it_creates_one_StatefulBeanToCsvBuilder() {
            assertThat(
                mockedStatefulBeanToCsvBuilderConstruction.constructed()
            ).hasSize(1);
        }

        @Test
        void it_creates_the_StatefulBeanToCsvBuilder_with_the_writer() {
            assertThat(statefulBeanToCsvBuilderConstructorArgs)
                .containsExactly(writer);
        }

        @Test
        void it_configures_the_builder_with_the_mapping_strategy() {
            verify(statefulBeanToCsvBuilder).withMappingStrategy(strategy);
        }

        @Test
        void it_configures_the_builder_without_ordered_results() {
            verify(statefulBeanToCsvBuilder).withOrderedResults(false);
        }

        @Test
        void it_builds_the_StatefulBeanToCsvBuilder() {
            verify(statefulBeanToCsvBuilder).build();
        }

        @Test
        void it_writes_the_element_to_the_writer()
            throws CsvRequiredFieldEmptyException,
                   CsvDataTypeMismatchException {
            verify(beanToCsv).write(person);
        }

        @Test
        void it_closes_the_writer() throws IOException {
            verify(writer).close();
        }
    }

    @Nested
    class when_open_and_write_throws_an_exception {

        private Exception thrown;

        @BeforeEach
        void beforeEach() throws IOException, CsvRequiredFieldEmptyException,
                                 CsvDataTypeMismatchException {
            mockOpen();
            doThrow(CSV_EXCEPTION).when(beanToCsv).write(person);

            final OpenCsvSink<Person> sink = OpenCsvSink.of(Person.class);
            sink.open(channel);

            thrown = catchThrowableOfType(
                () -> sink.write(person),
                IOException.class
            );
        }

        @AfterEach
        void afterEach() {
            mockedStatefulBeanToCsvBuilderConstruction.close();
        }

        @Test
        void it_wraps_the_exception_appropriately() {
            assertThat(thrown).hasMessage("Failed to write CSV element");
        }

        @Test
        void it_contains_the_original_exception() {
            assertThat(thrown).hasCause(CSV_EXCEPTION);
        }
    }

    @Nested
    class when_flushed_without_open {

        @Test
        void it_does_not_error() throws IOException {
            final OpenCsvSink<Person> sink = OpenCsvSink.of(Person.class);
            sink.flush();
        }
    }
}
