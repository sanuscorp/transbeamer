package com.sanuscorp.transbeamer;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This format defines how to read and write to Google Cloud's PubSub topics.
 */
public final class PubsubFormat implements DataFormat {

    private @Nullable String topic;

    private @Nullable String subscription;

    private PubsubFormat() {
        // Intentionally empty
    }

    /**
     * Create a {@link DataFormat} instance with a given Pubsub topic.  The
     * format will be suitable for both reading and writing messages.
     * @param topic The topic to read or write to
     * @return The created data format.
     */
    public static PubsubFormat withTopic(final String topic) {
        final PubsubFormat format = new PubsubFormat();
        format.topic = topic;
        return format;
    }

    /**
     * Create a {@link DataFormat} instance with a given Pubsub topic provider.
     * The format will be suitable for both reading and writing messages.
     * @param topicProvider The provider that names the topic.
     * @return The created data format.
     */
    public static PubsubFormat withTopic(
        final ValueProvider<String> topicProvider
    ) {
        final PubsubFormat format = new PubsubFormat();
        format.topic = topicProvider.get();
        return format;
    }

    /**
     * Create a {@link DataFormat} instance configured to read from the given
     * GCP Pubsub subscription.  The created format will only be suitable for
     * reading.
     * @param subscription The subscription to read from.
     * @return The created data format.
     */
    public static PubsubFormat withSubscription(final String subscription) {
        final PubsubFormat format = new PubsubFormat();
        format.subscription = subscription;
        return format;
    }

    /**
     * Create a {@link DataFormat} instance configured to read from the provided
     * GCP Pubsub Subscription.  The created format will only be suitable for
     * reading.
     * @param subscriptionProvider The provider that will specify the
     * subscription.
     * @return The created data format.
     */
    public static PubsubFormat withSubscription(
        final ValueProvider<String> subscriptionProvider
    ) {
        final PubsubFormat format = new PubsubFormat();
        format.subscription = subscriptionProvider.get();
        return format;
    }

    @Override
    public String getName() {
        return "GCP Pubsub Topic: " + topic;
    }

    @Override
    public <T extends SpecificRecordBase> PTransform<
        @NonNull PBegin,
        @NonNull PCollection<T>
    > getReader(final Class<T> clazz) {
        final PubsubIO.Read<T> avroReader = PubsubIO.readAvros(clazz);
        if (subscription != null) {
            return avroReader.fromSubscription(subscription);
        }

        return avroReader.fromTopic(topic);
    }

    @Override
    public <T extends GenericRecord> PTransform<
        @NonNull PCollection<T>,
        @NonNull PDone
    > getWriter(final Class<T> clazz) {
        if (topic == null) {
            throw new IllegalStateException(
                "Creating a Pubsub writer requires a GCP Topic, not a Subscription"
            );
        }

        return PubsubIO.writeAvros(clazz).to(topic);
    }
}
