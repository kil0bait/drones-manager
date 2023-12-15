package com.musala.artemis.dronemanager;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DoubleListConverter extends SimpleArgumentConverter {
    @Override
    protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        if (List.class.isAssignableFrom(targetType)) {
            if (source == null)
                return Collections.EMPTY_LIST;
            if (source instanceof String s)
                return Arrays.stream(s.split("\\s*:\\s*")).map(Double::parseDouble).collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Conversion of %s to %s not supported".formatted(source, targetType));
    }
}
