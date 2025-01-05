package ch.fhnw.richards.aigs_spring_server.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Converter
public class KnownCardsConverter implements AttributeConverter<ArrayList<Map<String, Integer>>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ArrayList<Map<String, Integer>> attribute) {
        if (attribute == null) return null;
        try {
            // In JSON umwandeln
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // Fehlerbehandlung nach Bedarf
            throw new RuntimeException("Fehler beim Serialisieren von knownCards", e);
        }
    }

    @Override
    public ArrayList<Map<String, Integer>> convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            // JSON zurück in ArrayList<Map<String, Integer>> wandeln
            return objectMapper.readValue(dbData, new TypeReference<ArrayList<Map<String, Integer>>>() {});
        } catch (JsonProcessingException e) {
            // Fehlerbehandlung nach Bedarf
            throw new RuntimeException("Fehler beim Deserialisieren von knownCards", e);
        }
    }
}
