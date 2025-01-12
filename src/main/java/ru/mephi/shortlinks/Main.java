package ru.mephi.shortlinks;

import ru.mephi.shortlinks.controller.LinkController;
import ru.mephi.shortlinks.repository.impl.LinkRepository;
import ru.mephi.shortlinks.service.LinkService;

public class Main {
    public static void main(String[] args) {

        // Создаем экземпляр репозитория
        LinkRepository repository = new LinkRepository();

        // Создаем экземпляр сервиса
        LinkService linkService = new LinkService(repository);

        // Создаем экземпляр контроллера
        LinkController controller = new LinkController(linkService);

        // Реализация сохранения данных при завершении программы
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            repository.saveDataOnExit();
            System.out.println("Данные сохранены в JSON-файл.");
        }));

        // Запускаем приложение
        controller.start();
    }
}
