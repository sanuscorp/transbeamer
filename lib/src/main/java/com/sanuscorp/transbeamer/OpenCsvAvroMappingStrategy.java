package com.sanuscorp.transbeamer;

import com.opencsv.bean.HeaderNameBaseMappingStrategy;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.util.List;

/**
 * This class provides the Header Mapping Strategy used to translate between a
 * CSV file and an Avro schema.
 * @param <T> The {@link GenericRecord} Avro type that will be populated at
 * runtime.  This is typically a generated class.
 */
public final class OpenCsvAvroMappingStrategy<T extends GenericRecord> extends
    HeaderNameBaseMappingStrategy<T> {

    private final Schema schema;

    private OpenCsvAvroMappingStrategy(final Class<T> clazz) {
        schema = ReflectUtils.getClassSchema(clazz);
        setType(clazz);
    }

    static <T extends GenericRecord> OpenCsvAvroMappingStrategy<T> of(final Class<T> clazz) {
        return new OpenCsvAvroMappingStrategy<>(clazz);
    }

    @Override
    public String[] generateHeader(final T bean) {
        if (headerIndex.isEmpty()) {
            final List<Schema.Field> fields = schema.getFields();
            final String[] header = new String[fields.size()];
            for (int i = 0; i < fields.size(); i++) {
                header[i] = fields.get(i).name();
            }
            headerIndex.initializeHeaderIndex(header);
            return header;
        }

        return headerIndex.getHeaderIndex();
    }
}
