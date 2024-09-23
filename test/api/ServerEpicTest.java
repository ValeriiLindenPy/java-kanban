package api;

import api.hendlers.typeTokens.EpicListTypeToken;
import api.hendlers.typeTokens.SubtasksListTypeToken;
import model.Epic;
import model.Subtask;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerEpicTest {
    private HttpTaskServer server;

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
        Epic task = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        String taskJson = ServerSettings.gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = ServerSettings.manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }


    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        ServerSettings.manager.createEpicTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем

        List<Epic> parsedTask = ServerSettings.gson.fromJson(response.body(),
                new EpicListTypeToken().getType());

        assertEquals(1, parsedTask.size(), "Некорректное количество задач");
        assertEquals("Test 2", parsedTask.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        int initialId = ServerSettings.manager.createEpicTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + initialId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Epic parsedTask = ServerSettings.gson.fromJson(response.body(),
                Epic.class);
        assertEquals(parsedTask.getId(), initialId);
    }

    @Test
    public void testGetTaskById404() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Test 2", "Testing task 2");
        // конвертируем её в JSON
        int initialId = ServerSettings.manager.createEpicTask(task);

        assertEquals(1, ServerSettings.manager.getEpics().size());

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + initialId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertEquals(0, ServerSettings.manager.getEpics().size());
    }

    @Test
    public void testGetEpicSubtasksById() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic("Epic 1", "Testing epic 1");
        // конвертируем её в JSON
        int epicId = ServerSettings.manager.createEpicTask(task);
        Subtask subtask = new Subtask("Test 2", "Testing Subtask 2",
                Duration.ofMinutes(5), LocalDateTime.now());
        subtask.setEpicId(epicId);
        int subtaskId = ServerSettings.manager.createSubTask(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Subtask> parsedTask = ServerSettings.gson.fromJson(response.body(),
                new SubtasksListTypeToken().getType());

        assertEquals(parsedTask.get(0).getId(), subtaskId);
    }
}