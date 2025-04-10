
package com.sanuscorp.transbeamer;

/**
 * This enumeration indicates the file formats we support for reading and
 * writing.
 */
public enum BuiltInFormat {

    /**
     * The Avro format.
     */
    AVRO(new AvroFormat()),

    /**
     * The Comma-Separated Value Format.
     */
    CSV(new CsvFormat()),

    /**
     * The Parquet format.
     */
    PARQUET(new ParquetFormat());

    private final FileFormat format;

    BuiltInFormat(final FileFormat format) {
        this.format = format;
    }

    FileFormat getFormat() {
        return format;
    }
}
