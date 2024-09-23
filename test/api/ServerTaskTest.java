package api;

import api.hendlers.typeTokens.TasksListTypeToken;
import model.Task;
import model.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerTaskTest {
    private HttpTaskServer server;
    int taskCounter = ServerSettings.manager.getTasks().size();

    @BeforeEach
    void setInitState() throws IOException {
        server = new HttpTaskServer();
        server.start();
    }

    @AfterEach
    void stopServer() throws IOException {
        ServerSettings.manager.deleteTasks();
        ServerSettings.manager.deleteSubTasks();
        ServerSettings.manager.deleteEpics();
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = ServerSettings.gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = ServerSettings.manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }


    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        ServerSettings.manager.createTask(task);

        // Добавляем задачу в менеджер
        System.out.println("Created task, current task count: " + ServerSettings.manager.getTasks().size());

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем REST, отвечающий за получение задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> parsedTask = ServerSettings.gson.fromJson(response.body(), new TasksListTypeToken().getType());
        System.out.println("Parsed tasks from server: " + parsedTask);
        assertEquals(1, parsedTask.size(), "Некорректное количество задач");
        assertEquals("Test 2", parsedTask.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTask406() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test 3", "Testing task 3",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = ServerSettings.gson.toJson(task);
        String taskJson2 = ServerSettings.gson.toJson(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response2.statusCode());
    }


    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        int initialId = ServerSettings.manager.createTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + initialId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Task parsedTask = ServerSettings.gson.fromJson(response.body(),
                Task.class);
        assertEquals(parsedTask.getId(), initialId);
    }

    @Test
    public void testGetTaskById404() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int initialId = ServerSettings.manager.createTask(task);

        assertEquals(1, ServerSettings.manager.getTasks().size());

        // Use the actual ID of the task created
        URI url = URI.create("http://localhost:8080/tasks/" + initialId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(0, ServerSettings.manager.getTasks().size());
    }
}