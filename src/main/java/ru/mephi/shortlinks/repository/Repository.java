package ru.mephi.shortlinks.repository;

import ru.mephi.shortlinks.model.User;

import java.util.List;
import java.util.Optional;

public interface Repository {
    Optional<User> findUserById(String userId);

    void saveUser(User user);

    void deleteUser(String userId);

    List<User> findAllUsers();
}

