package ru.mephi.shortlinks.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("Конфигурационный файл 'config.properties' не найден");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке файла конфигурации: " + e.getMessage());
            throw new RuntimeException("Ошибка при загрузке конфигурации " + e.getMessage());
        }
    }

    // Получение максимального срока действия ссылки из конфигурационного файла
    public static int getMaxExpiryDays() {
        int days = Integer.parseInt(properties.getProperty("link.max.expiry.days", "30"));
        if (days <= 0) {
            throw new IllegalArgumentException("Максимальный срок действия ссылки должен быть положительным числом.");
        }
        return days;
    }

    // Получение максимального количества переходов по ссылке из конфигурационного файла
    public static int getDefaultMaxClicks() {
        int clicks = Integer.parseInt(properties.getProperty("link.default.maxClicks", "50"));
        if (clicks <= 0) {
            throw new IllegalArgumentException("Максимальное количество переходов по ссылке должно быть положительным числом.");
        }
        return clicks;
    }

    // Получение префикса для коротких ссылок из конфигурационного файла
    public static String getPrefix(String prefix) {
        return properties.getProperty(prefix, "myclck.ru/");
    }
}
