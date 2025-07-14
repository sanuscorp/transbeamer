package com.sanuscorp.transbeamer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.io.Serializable;
import java.lang.reflect.Method;

final class ReflectUtils implements Serializable {

    private ReflectUtils() {
        // Intentionally Empty
    }

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
     * @throws IllegalArgumentException if we cannot get the Avro schema from
     * the given class.
     */
    static <T extends GenericRecord> Schema getClassSchema(final Class<T> clazz) {
        try {
            final Method getClassSchema = clazz.getMethod("getClassSchema");
            return (Schema) getClassSchema.invoke(null);
        } catch (ReflectiveOperationException roEx) {
            throw new IllegalArgumentException(
                "'getClassSchema' cannot be invoked on " + clazz.getSimpleName(),
                roEx
            );
        }
    }
}
