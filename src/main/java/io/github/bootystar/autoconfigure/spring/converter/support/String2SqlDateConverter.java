package io.github.bootystar.autoconfigure.spring.converter.support;

import io.github.bootystar.autoconfigure.spring.converter.DateTimeConverter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author bootystar
 */
public class String2SqlDateConverter implements DateTimeConverter<String, Date> {
    private final DateTimeFormatter formatter;

    public String2SqlDateConverter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public Date convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        return Date.valueOf(LocalDate.parse(source, formatter));
    }

}
