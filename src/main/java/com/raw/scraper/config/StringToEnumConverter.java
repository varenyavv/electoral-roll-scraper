package com.raw.scraper.config;

import com.raw.scraper.constant.NepalState;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, NepalState> {
    @Override
    public NepalState convert(String source) {
        return NepalState.valueOf(source.toUpperCase());
    }
}