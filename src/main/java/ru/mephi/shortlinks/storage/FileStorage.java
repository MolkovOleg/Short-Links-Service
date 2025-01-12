package ru.mephi.shortlinks.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.mephi.shortlinks.model.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileStorage {
    private static final String FINAL_PATH = "src/main/resources/users_data.json";
    private final ObjectMapper objectMapper;

    public FileStorage() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Подключаем модуль для работы с датами
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Используем человекочитаемые даты
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Красивый вывод JSON-файла
    }

    // Сохранение данных в JSON-файл
    public void saveDataToFile(List<User> users) {
        try {
            objectMapper.writeValue(new File(FINAL_PATH), users);
            System.out.println("Данные пользователей сохранены в JSON-файл: " + FINAL_PATH);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения данных в JSON-файл: " + e.getMessage());
        }
    }

    // Загрузка данных из JSON-файла
    public List<User> loadDataFromFile() {
        try {
            return List.of(objectMapper.readValue(new File(FINAL_PATH), User[].class));
        } catch (IOException e) {
            System.out.println("Файл данных пользователей не найден или повреждён. Начинаем с пустого хранилища.");
            return List.of();
        }
    }
}
