package com.sanuscorp.transbeamer;

import com.google.gson.Gson;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provides a {@link DoFn} that converts a line of JSON into a Java
 * element.
 * @param <T> The type of the element to create.
 */
public final class NDJsonReaderFn<T> extends DoFn<
    String,
    T
> {
    private static final Logger LOG = LogManager.getLogger(NDJsonReaderFn.class);

    private final Class<T> clazz;

    private transient Gson gson;

    private NDJsonReaderFn(final Class<T> clazz) {
        this.clazz = clazz;
    }

    static <T> ParDo.SingleOutput<
        String,
        T
    > parDoOf(final Class<T> clazz) {
        return ParDo.of(new NDJsonReaderFn<>(clazz));
    }

    @ProcessElement
    public void processElement(
        @Element final String input,
        final OutputReceiver<T> receiver
    ) {
        final T element = getGson().fromJson(input, clazz);
        LOG.trace("Read element {} into {}", input, element);
        receiver.output(element);
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
