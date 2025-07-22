package com.sanuscorp.transbeamer;

import com.sanuscorp.transbeamer.test.avro.Person;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link PubsubFormat} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("The PubsubFormat class")
public class PubsubFormatTests {

    // Fixtures
    private static final String TOPIC = "/my/test/topic";

    private static final String SUBSCRIPTION = "/my/test/subscription";

    // Dependencies
    @Mock
    private MockedStatic<PubsubIO> mockedPubsubIO;

    // Interim values
    @Mock(answer = Answers.RETURNS_SELF)
    private PubsubIO.Read<Person> avroReader;

    @Mock(answer = Answers.RETURNS_SELF)
    private PubsubIO.Write<Person> avroWriter;

    @Nested
    class when_created_with_a_topic_and_getting_a_reader {

        private PTransform<
            @NonNull PBegin,
            @NonNull PCollection<Person>
        > result;

        private PubsubFormat format;

        @BeforeEach
        void beforeEach() {
            mockedPubsubIO.when(() -> PubsubIO.readAvros(Person.class))
                .thenReturn(avroReader);

            format = PubsubFormat.withTopic(TOPIC);
            result = format.getReader(Person.class);
        }
        
        @Test
        void it_returns_the_expected_name() {
            assertThat(format.getName())
                .isEqualTo("GCP Pubsub Topic: " + TOPIC);
        }

        @Test
        void it_creates_one_avro_reader() {
            mockedPubsubIO.verify(() -> PubsubIO.readAvros(Person.class));
        }

        @Test
        void it_creates_a_transform_from_the_topic() {
            verify(avroReader).fromTopic(TOPIC);
        }

        @Test
        void it_returns_the_expected_reader() {
            assertThat(result).isEqualTo(avroReader);
        }
    }

    @Nested
    class when_created_with_a_topic_provider_and_getting_a_reader {

        @Mock
        private ValueProvider<String> topicProvider;

        private PTransform<
            @NonNull PBegin,
            @NonNull PCollection<Person>
        > result;

        @BeforeEach
        void beforeEach() {
            when(topicProvider.get()).thenReturn(TOPIC);
            mockedPubsubIO.when(() -> PubsubIO.readAvros(Person.class))
                .thenReturn(avroReader);

            result = PubsubFormat.withTopic(topicProvider).getReader(Person.class);
        }

        @Test
        void it_creates_a_transform_from_the_topic() {
            verify(avroReader).fromTopic(TOPIC);
        }

        @Test
        void it_returns_the_expected_reader() {
            assertThat(result).isEqualTo(avroReader);
        }
    }

    @Nested
    class when_created_with_a_subscription_and_getting_a_reader {

        private PTransform<
            @NonNull PBegin,
            @NonNull PCollection<Person>
        > result;

        @BeforeEach
        void beforeEach() {
            mockedPubsubIO.when(() -> PubsubIO.readAvros(Person.class))
                .thenReturn(avroReader);

            result = PubsubFormat.withSubscription(SUBSCRIPTION).getReader(Person.class);
        }

        @Test
        void it_creates_one_avro_reader() {
            mockedPubsubIO.verify(() -> PubsubIO.readAvros(Person.class));
        }

        @Test
        void it_creates_a_transform_from_the_subscription() {
            verify(avroReader).fromSubscription(SUBSCRIPTION);
        }

        @Test
        void it_returns_the_expected_reader() {
            assertThat(result).isEqualTo(avroReader);
        }
    }

    @Nested
    class when_created_with_a_subscription_provider_and_getting_a_reader {
        @Mock
        private ValueProvider<String> subscriptionProvider;

        private PTransform<
            @NonNull PBegin,
            @NonNull PCollection<Person>
        > result;

        @BeforeEach
        void beforeEach() {
            when(subscriptionProvider.get()).thenReturn(SUBSCRIPTION);
            mockedPubsubIO.when(() -> PubsubIO.readAvros(Person.class))
                .thenReturn(avroReader);

            result = PubsubFormat.withSubscription(subscriptionProvider)
                .getReader(Person.class);
        }

        @Test
        void it_creates_a_transform_from_the_subscription() {
            verify(avroReader).fromSubscription(SUBSCRIPTION);
        }

        @Test
        void it_returns_the_expected_reader() {
            assertThat(result).isEqualTo(avroReader);
        }
    }

    @Nested
    class when_created_with_a_topic_and_getting_a_writer {
        private PTransform<
            @NonNull PCollection<Person>,
            @NonNull PDone
        > result;

        @BeforeEach
        void beforeEach() {
            mockedPubsubIO.when(() -> PubsubIO.writeAvros(Person.class))
                .thenReturn(avroWriter);

            result = PubsubFormat.withTopic(TOPIC).getWriter(Person.class);
        }

        @Test
        void it_creates_one_avro_writer() {
            mockedPubsubIO.verify(() -> PubsubIO.writeAvros(Person.class));
        }

        @Test
        void it_creates_a_transform_from_the_topic() {
            verify(avroWriter).to(TOPIC);
        }

        @Test
        void it_returns_the_expected_writer() {
            assertThat(result).isEqualTo(avroWriter);
        }
    }

    @Nested
    class when_created_with_a_subscription_and_getting_a_writer {
        private Throwable thrown;

        @BeforeEach
        void beforeEach() {
            final PubsubFormat format = PubsubFormat.withSubscription(
                SUBSCRIPTION
            );
            thrown = catchThrowableOfType(
                () -> format.getWriter(Person.class),
                IllegalStateException.class
            );
        }

        @Test
        void it_throws_the_expected_exception() {
            assertThat(thrown).hasMessage(
                "Creating a Pubsub writer requires a GCP Topic, not a Subscription"
            );
        }
    }

}
