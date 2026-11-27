package com.genai.posture.dataaccess.entity.converter;

import jakarta.persistence.AttributeConverter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.Converter;

import java.util.Map;

/**
 * com.fasterxml.jackson.core.type.TypeReference does not exist because your Spring
 * Boot 4 setup uses Jackson 3, which moved core and databind packages from com.fasterxml.jackson.* to tools.jackson.*.Fix MapJsonbConverter.java for Jackson 3
 *
 * import tools.jackson.databind.ObjectMapper;
 * import tools.jackson.databind.json.JsonMapper;
 *
 */
@Converter
public class MapJsonbConverter implements AttributeConverter<Map<String, Object>, String> {
    private final ObjectMapper mapper;

    public MapJsonbConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return attribute == null ? null : mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON serialize failed", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : mapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON parse failed", e);
        }
    }
}