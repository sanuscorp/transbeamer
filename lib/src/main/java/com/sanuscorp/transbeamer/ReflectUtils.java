package com.sanuscorp.transbeamer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;
import java.lang.reflect.Method;

final class ReflectUtils implements Serializable {

    /**
     * Create a new instance of a given class, using the default/no-arg
     * constructor.
     * @param clazz The class to create the instance of
     * @return The newly created instance
     * @param <T> The type of the class itself
     * @throws IllegalArgumentException If the given class does not have a
     * no-arg constructor, or if that constructor throws an exception.
     */
    static <T extends GenericRecord> T instantiate(
        final Class<T> clazz
    ) throws IllegalArgumentException {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException roEx) {
            throw new IllegalArgumentException(
                "Class " + clazz.getSimpleName() + " cannot be instantiated",
                roEx
            );
        }
    }

    /**
     * Gets the Schema from an Avro-generated class.
     * @param clazz The avro-generated class.
     * @return The Schema for clazz
     */
    static <T extends GenericRecord> Schema getClassSchema(final Class<T> clazz) {
        try {
            final Method getClassSchema = clazz.getMethod("getClassSchema");
            final Object schema = getClassSchema.invoke(null);
            if (schema instanceof Schema) {
                return (Schema) schema;
            } else {
                throw new IllegalArgumentException(
                    "Expected Avro Schema but got " + schema
                );
            }
        } catch (ReflectiveOperationException roEx) {
            throw new IllegalArgumentException(
                "'getClassSchema' cannot be invoked on " + clazz.getSimpleName(),
                roEx
            );
        }
    }
}
