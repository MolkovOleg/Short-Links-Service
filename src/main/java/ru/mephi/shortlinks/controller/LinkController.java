package ru.mephi.shortlinks.controller;

import ru.mephi.shortlinks.exceptions.UserNotFoundException;
import ru.mephi.shortlinks.model.ShortLink;
import ru.mephi.shortlinks.model.User;
import ru.mephi.shortlinks.service.LinkService;

import java.util.List;
import java.util.Scanner;

public class LinkController {

    private final LinkService linkService;
    private final Scanner scanner = new Scanner(System.in);


    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    public void start() {
        while (true) {
            try {
                displayMenu();
                int operation = scanner.nextInt();
                scanner.nextLine();

                switch (operation) {
                    case 1 -> createShortLink();
                    case 2 -> showAllLinks();
                    case 3 -> deleteLink();
                    case 4 -> updateClickLimit();
                    case 5 -> redirectLink();
                    case 6 -> setUser();
                    case 7 -> createUser();
                    case 8 -> {
                        System.out.println("Программа завершена. До скорой встречи!");
                        System.exit(0);
                    }
                    default -> System.out.println("Неверная операция. Повторите попытку.");
                }
            } catch (Exception e) {
                System.out.println("\nОшибка: " + e.getMessage());
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n=== Сервис коротких ссылок ===");
        System.out.println("\n=== Меню ===");
        System.out.println("1. Создать короткую ссылку");
        System.out.println("2. Показать все ссылки пользователя");
        System.out.println("3. Удалить ссылку");
        System.out.println("4. Изменить лимит переходов");
        System.out.println("5. Переход по короткой ссылке");
        System.out.println("6. Сменить пользователя");
        System.out.println("7. Создать пользователя");
        System.out.println("8. Выйти");
        System.out.print("\nВыберите номер действия: ");
    }

    private void createUser() {
        try {
            linkService.createUser();
            System.out.println("\nНовый пользователь создан!");
            setUser();
        } catch (Exception e) {
            System.out.println("\nОшибка при создании пользователя: " + e.getMessage());
        }
    }

    private void setUser() {
        try {
            System.out.println("Список доступных пользователей:");
            List<User> users = linkService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("Нет доступных пользователей. Создайте нового пользователя.");
                return;
            }

            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". Пользователь ID: " + users.get(i).getUserId());
            }

            System.out.print("Введите номер пользователя: ");
            int userIndex = scanner.nextInt();
            scanner.nextLine();

            if (userIndex < 1 || userIndex > users.size()) {
                System.out.println("Неверный индекс пользователя.");
                return;
            }

            String selectedUserId = users.get(userIndex - 1).getUserId();
            linkService.setCurrentUser(selectedUserId);
            System.out.println("Текущий пользователь установлен: " + selectedUserId);
        } catch (UserNotFoundException e) {
            System.out.println("\nОшибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nОшибка при установке пользователя: " + e.getMessage());
        }
    }

    private void createShortLink() {
        try {
            System.out.print("Введите URL-ссылку: ");
            String originalUrl = scanner.nextLine();

            System.out.print("Введите лимит переходов: ");
            int maxClicks = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Введите срок действия (в днях): ");
            int expiryDays = scanner.nextInt();
            scanner.nextLine();

            String shortUrl = linkService.createShortLink(originalUrl, maxClicks, expiryDays);
            System.out.println("\nСоздана новая короткая ссылка: " + shortUrl);
        } catch (Exception e) {
            System.out.println("\nОшибка: " + e.getMessage());
        }
    }

    private void showAllLinks() {
        try {
            List<ShortLink> links = linkService.getAllLinks();

            if (links.isEmpty()) {
                System.out.println("\nУ вас пока нет доступных ссылок.");
            } else {
                System.out.println("\nВаши ссылки:\n");
                for (int i = 0; i < links.size(); i++) {
                    System.out.println((i + 1) + ". " + links.get(i));
                }
            }
        } catch (Exception e) {
            System.out.println("\nОшибка: " + e.getMessage());
        }
    }

    private void deleteLink() {
        try {
            System.out.print("Введите короткую ссылку: ");
            String shortUrl = scanner.nextLine();

            linkService.deleteLink(shortUrl);
            System.out.println("\nСсылка удалена!");
        } catch (Exception e) {
            System.out.println("\nОшибка: " + e.getMessage());
        }
    }

    private void updateClickLimit() {
        try {
            System.out.print("Введите короткую ссылку: ");
            String shortUrl = scanner.nextLine();

            System.out.print("Введите новый лимит переходов: ");
            int maxClicks = scanner.nextInt();
            scanner.nextLine();

            linkService.updateMaxClicks(shortUrl, maxClicks);
            System.out.println("\nЛимит переходов обновлен!");
        } catch (Exception e) {
            System.out.println("\nОшибка: " + e.getMessage());
        }
    }

    private void redirectLink() {
        try {
            System.out.print("Введите короткую ссылку: ");
            String shortUrl = scanner.nextLine();

            linkService.openOriginalUrl(shortUrl);

            ShortLink shortLink = linkService.getLinkByShortUrl(shortUrl);

            System.out.println("Оставшееся количество переходов: " + shortLink.getRemainingClicks());
        } catch (Exception e) {
            System.out.println("\nОшибка: " + e.getMessage());
        }
    }
}
