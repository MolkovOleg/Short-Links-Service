package ru.mephi.shortlinks.repository.impl;

import ru.mephi.shortlinks.model.User;
import ru.mephi.shortlinks.repository.Repository;
import ru.mephi.shortlinks.storage.FileStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LinkRepository implements Repository {

    // Создаем коллекцию пользователей ID и объектов User
    private final Map<String, User> users = new HashMap<>();
    private final FileStorage fileStorage = new FileStorage();

    public LinkRepository() {
        // Загружаем данные из JSON-файла при создании репозитория
        List<User> loadedUsers = fileStorage.loadDataFromFile();
        loadedUsers.forEach(user -> users.put(user.getUserId(), user));
        System.out.println("Данные пользователей загружены из JSON-файла.");
    }

    @Override
    public Optional<User> findUserById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void saveUser(User user) {
        users.put(user.getUserId(), user);
    }

    @Override
    public void deleteUser(String userId) {
        users.remove(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return List.copyOf(users.values());
    }

    public void saveDataOnExit() {
        fileStorage.saveDataToFile(findAllUsers());
    }
}
