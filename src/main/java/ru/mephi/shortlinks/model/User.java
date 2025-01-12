package ru.mephi.shortlinks.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;  // ID пользователя
    private List<ShortLink> shortLinks;  // Список ссылок

    public User(String userId) {
        this.userId = userId;
        this.shortLinks = new ArrayList<>();
    }

    // Функционал добавления ссылки пользователя
    public void addLink(ShortLink shortLink) {
        if (shortLinks.stream()
                .anyMatch(link -> link.getOriginalUrl().equals(shortLink.getOriginalUrl()))) {
            throw new IllegalArgumentException("Ссылка на данный URL уже существует.");
        }
        shortLinks.add(shortLink);
    }

    // Удаление ссылки пользователя
    public void removeLink(String shortUrl) {
        shortLinks.removeIf(link -> link.getShortUrl().equals(shortUrl));
    }

    // Поиск по короткой ссылке
    public Optional<ShortLink> findLinkByShortUrl(String shortUrl) {
        return shortLinks.stream()
                .filter(link -> link.getShortUrl().equals(shortUrl))
                .findFirst();
    }
}
