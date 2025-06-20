package com.journal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.journal.model.Journal;

import java.io.File;
import java.io.IOException;

public class JsonSerializer {
    private static final String JOURNAL_JSON = "data/journal.json";
    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // для поддержки LocalDate
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // включаем pretty printing
    }

    public void serialize(Journal journal) throws IOException {
        File file = new File(JOURNAL_JSON);
        System.out.println("Сохранение в JSON: " + file.getAbsolutePath());
        objectMapper.writeValue(file, journal);
    }

    public Journal deserialize() throws IOException {
        File file = new File(JOURNAL_JSON);
        System.out.println("Попытка загрузки из JSON: " + file.getAbsolutePath() + 
                         ", существует: " + file.exists() + 
                         ", размер: " + file.length());
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        Journal journal = objectMapper.readValue(file, Journal.class);
        journal.restoreRelations();
        return journal;
    }
}
