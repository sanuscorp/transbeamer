package com.sanuscorp.transbeamer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

final class ConvertGenericToSpecificFn<
    T extends SpecificRecordBase
> extends DoFn<
    @NonNull GenericRecord,
    @NonNull T
> {
    private static final Logger LOG = LogManager.getLogger(
        ConvertGenericToSpecificFn.class
    );

    /**
     * The class we will convert {@link GenericRecord} instances to.
     */
    private final Class<T> clazz;

    private List<String> fieldNames;

    ConvertGenericToSpecificFn(Class<T> clazz) {
        this.clazz = clazz;
        LOG.debug("Created to convert GenericRecord to {}", clazz.getSimpleName());
    }

    static <T extends SpecificRecordBase> ParDo.SingleOutput<
        GenericRecord,
        T
    > parDoOf(final Class<T> clazz) {
        final ConvertGenericToSpecificFn<T> converter =
            new ConvertGenericToSpecificFn<>(clazz);
        return ParDo.of(converter);
    }

    @ProcessElement
    public void processElement(
        @Element final GenericRecord input,
        final OutputReceiver<T> receiver
    ) {
        LOG.trace("Processing GenericRecord to {}", clazz);
        final T avroRecord = ReflectUtils.instantiate(clazz);
        getFieldNames().forEach(fieldName -> {
            final Object value = input.get(fieldName);
            avroRecord.put(fieldName, value);
        });

        LOG.trace("Converted GenericRecord {} to {}", input, avroRecord);
        receiver.output(avroRecord);
    }

    private List<String> getFieldNames() {
        if (fieldNames == null) {
            final Schema schema = ReflectUtils.getClassSchema(clazz);
            final List<Schema.Field> fields = schema.getFields();
            fieldNames = new ArrayList<>(fields.size());
            fields.forEach((field) -> fieldNames.add(field.name()));
        }
        return fieldNames;
    }
}
