package io.github.bootystar.autoconfigure.spring.converter.support;

import io.github.bootystar.autoconfigure.spring.converter.DateTimeConverter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author bootystar
 */
public class String2SqlTimestampConverter implements DateTimeConverter<String, Timestamp> {
    private final DateTimeFormatter formatter;

    public String2SqlTimestampConverter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public Timestamp convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        return Timestamp.valueOf(LocalDateTime.parse(source, formatter));
    }
}
