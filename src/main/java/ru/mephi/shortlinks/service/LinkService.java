package ru.mephi.shortlinks.service;

import ru.mephi.shortlinks.config.ConfigLoader;
import ru.mephi.shortlinks.exceptions.LinkNotFoundException;
import ru.mephi.shortlinks.exceptions.UserNotFoundException;
import ru.mephi.shortlinks.model.ShortLink;
import ru.mephi.shortlinks.model.User;
import ru.mephi.shortlinks.repository.impl.LinkRepository;

import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class LinkService {

    private final LinkRepository repository;
    private String currentUserId;

    public LinkService(LinkRepository repository) {
        this.repository = repository;
    }

    // Метод генерации короткой ссылки
    private String generateShortUrl(String originalUrl) {
        String prefix = ConfigLoader.getPrefix("link.short.url.prefix");
        String uniquePart = UUID.randomUUID().toString().substring(0, 6);
        return prefix + Integer.toHexString(originalUrl.hashCode()).substring(0, 6) + uniquePart;
    }

    // Создание короткой ссылки
    public String createShortLink(String originalUrl, int userMaxClicks, int userExpiryDays) {

        User user = getCurrentUser();

        cleanExpiredOrExceededLinks(user);

        String shortUrl = generateShortUrl(originalUrl);

        // Определение и установка максимального количества переходов и срока действия ссылки
        int expiryDays = Math.min(userExpiryDays, ConfigLoader.getMaxExpiryDays());
        int maxClicks = Math.min(userMaxClicks, ConfigLoader.getDefaultMaxClicks());

        // Создание новой ссылки с использованием Builder
        ShortLink shortlink = ShortLink.builder()
                .originalUrl(originalUrl)
                .shortUrl(shortUrl)
                .maxClicks(maxClicks)
                .createdAt(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusDays(expiryDays))
                .build();

        user.addLink(shortlink);
        repository.saveUser(user);

        return shortUrl;
    }

    // Создание нового пользователя
    public User createUser() {
        String userId;

        do {
            userId = UUID.randomUUID().toString();
            System.out.println("Генерируем userId: " + userId);
        } while (repository.findUserById(userId).isPresent());

        User newUser = new User(userId);
        System.out.println("Сохраняем пользователя с ID: " + userId);
        repository.saveUser(newUser);
        this.currentUserId = userId;

        return newUser;
    }

    // Удаление текущего пользователя
    public void deleteCurrentUser() {
        if (currentUserId == null) {
            throw new IllegalStateException("Текущий пользователь не установлен.");
        }
        repository.deleteUser(currentUserId);
        currentUserId = null;
    }

    // Установка текущего пользователя
    public void setCurrentUser(String userId) throws UserNotFoundException {
        if (repository.findUserById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        this.currentUserId = userId;
    }

    // Получение текущего пользователя
    public User getCurrentUser() {
        if (currentUserId == null) {
            System.out.println("Текущий пользователь не установлен. Создаем нового пользователя.");
            return createUser();
        }

        return repository.findUserById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("Пользователь с текущим ID не найден."));
    }


    // Получение списка всех пользователей
    public List<User> getAllUsers() {
        return repository.findAllUsers();
    }

    // Метод удаления устаревших и исчерпанных ссылок
    private void cleanExpiredOrExceededLinks(User user) {
        user.getShortLinks().removeIf(link -> link.isExpired() || link.isLimitExceeded());
        repository.saveUser(user);
    }

    // Получение всех ссылок пользователя
    public List<ShortLink> getAllLinks() {
        User user = getCurrentUser();
        cleanExpiredOrExceededLinks(user);
        return user.getShortLinks();
    }

    // Получение ссылки пользователя по короткой ссылке
    public ShortLink getLinkByShortUrl(String shortUrl) throws LinkNotFoundException {
        User user = getCurrentUser();
        return user.findLinkByShortUrl(shortUrl)
                .orElseThrow(() -> new LinkNotFoundException("Ссылка не найдена."));
    }

    // Удаление ссылки пользователя
    public void deleteLink(String shortUrl) {
        User user = getCurrentUser();
        user.removeLink(shortUrl);
        repository.saveUser(user);
    }

    // Обновление лимита переходов по определенной ссылке
    public void updateMaxClicks(String shortUrl, int maxClicks) throws LinkNotFoundException {
        User user = getCurrentUser();
        ShortLink shortLink = user.findLinkByShortUrl(shortUrl)
                .orElseThrow(() -> new LinkNotFoundException("Ссылка не найдена."));

        shortLink.setMaxClicks(maxClicks);
        repository.saveUser(user);
    }

    // Переход по короткой ссылке
    public String redirect(String shortUrl) throws LinkNotFoundException {
        User user = getCurrentUser();
        cleanExpiredOrExceededLinks(user);

        ShortLink link = user.findLinkByShortUrl(shortUrl)
                .orElseThrow(() -> new LinkNotFoundException("Ссылка не найдена."));

        if (link.isExpired()) {
            throw new LinkNotFoundException("Срок действия ссылки истек!");
        }

        if (link.isLimitExceeded()) {
            throw new LinkNotFoundException("Лимит переходов исчерпан!");
        }

        link.incrementClickCount();
        return link.getOriginalUrl();
    }

    // Открытие URL-ссылки в браузере
    public void openOriginalUrl(String shortUrl) throws Exception {
        String originalUrl = redirect(shortUrl);

        // Открываем ссылку в браузере
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(originalUrl));
            System.out.println("Переход по ссылке: " + originalUrl);
        } else {
            throw new UnsupportedOperationException("Невозможно открыть ссылку в браузере.");
        }
    }
}
