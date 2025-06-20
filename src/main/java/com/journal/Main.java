package com.journal;

import com.journal.service.JournalService;
import com.journal.service.DataValidator;
import com.journal.model.Journal;
import com.journal.ui.ConsoleUI;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Загружаем журнал через JournalService
        JournalService journalService = new JournalService();
        Journal journal = journalService.getJournal();
        
        // Валидация данных
        DataValidator validator = new DataValidator();
        var errors = validator.validate(journal);
        
        if (!errors.isEmpty()) {
            System.out.println("Найдены ошибки в данных:");
            System.out.println("----------------");
            errors.forEach(System.out::println);
            System.out.println("----------------");
            return;
        }
        
        // Запускаем консольный интерфейс
        ConsoleUI ui = new ConsoleUI(journalService);
        ui.start();
    }
}
