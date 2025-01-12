package ru.mephi.shortlinks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLink {
    private String originalUrl;
    private String shortUrl;
    private int maxClicks;
    private int clickCount = 0;
    private LocalDateTime createdAt;
    private LocalDateTime expireTime;

    // Проверка на истечении срока действия ссылки
    @JsonIgnore
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    // Проверка на превышение лимита переходов
    @JsonIgnore
    public boolean isLimitExceeded() {
        return clickCount >= maxClicks;
    }

    // Увеличение счетчика перехода
    public void incrementClickCount() {
        if (isLimitExceeded()) {
            throw new IllegalArgumentException("Лимит переходов исчерпан.");
        }
        this.clickCount++;
    }

    // Метод для отображения остатка доступных переходов
    @JsonIgnore
    public int getRemainingClicks() {
        return maxClicks - clickCount;
    }

    // Метод для отображения остатка жизни ссылки
    @JsonIgnore
    public long getRemainingTimeInMinutes() {
        return Duration.between(LocalDateTime.now(), expireTime).toMinutes();
    }

    @Override
    public String toString() {

        // Форматирование даты для удобного просмотра
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return "\nОригинальная ссылка: " + originalUrl + "\n" +
                "Короткая ссылка: " + shortUrl + "\n" +
                "Лимит переходов: " + maxClicks + "\n" +
                "Количество переходов: " + clickCount + "\n" +
                "Дата создания: " + createdAt.format(formatter) + "\n" +
                "Срок действия: " + expireTime.format(formatter) + "\n";
    }
}
