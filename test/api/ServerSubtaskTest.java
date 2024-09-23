package api;

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
class ServerSubtaskTest {
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
        Epic epic = new Epic("Epic 1", "Testing Epic 1");
        int epicId = ServerSettings.manager.createEpicTask(epic);
        Subtask task = new Subtask("Test 2", "Testing Subtask 2",
                  Duration.ofMinutes(5), LocalDateTime.now());
        task.setEpicId(epicId);
        // конвертируем её в JSON
        String taskJson = ServerSettings.gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = ServerSettings.manager.getSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }


    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic 1", "Testing Epic 1");
        int epicId = ServerSettings.manager.createEpicTask(epic);
        Subtask task = new Subtask("Test 2", "Testing Subtask 2",
                Duration.ofMinutes(5), LocalDateTime.now());
        task.setEpicId(epicId);
        // конвертируем её в JSON
        ServerSettings.manager.createSubTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем

        List<Subtask> parsedTask = ServerSettings.gson.fromJson(response.body(),
                new SubtasksListTypeToken().getType());

        assertEquals(1, parsedTask.size(), "Некорректное количество задач");
        assertEquals("Test 2", parsedTask.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTask406() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Epic 1", "Testing Epic 1");
        int epicId = ServerSettings.manager.createEpicTask(epic);
        Subtask task = new Subtask("Test 2", "Testing Subtask 2",
                Duration.ofMinutes(5), LocalDateTime.now());
        task.setEpicId(epicId);
        Subtask task2 = new Subtask("Test 3", "Testing Subtask 3",
                Duration.ofMinutes(5), LocalDateTime.now());
        task2.setEpicId(epicId);
        // конвертируем её в JSON
        String taskJson = ServerSettings.gson.toJson(task);
        String taskJson2 = ServerSettings.gson.toJson(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
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
        Epic epic = new Epic("Epic 1", "Testing Epic 1");
        int epicId = ServerSettings.manager.createEpicTask(epic);
        Subtask task = new Subtask("Test 2", "Testing Subtask 2",
                Duration.ofMinutes(5), LocalDateTime.now());
        task.setEpicId(epicId);
        // конвертируем её в JSON
        int initialId = ServerSettings.manager.createSubTask(task);
        System.out.println(initialId);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + initialId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Subtask parsedTask = ServerSettings.gson.fromJson(response.body(),
                Subtask.class);
        assertEquals(parsedTask.getId(), initialId);
    }

    @Test
    public void testGetTaskById404() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing Epic 1");
        int epicId = ServerSettings.manager.createEpicTask(epic);
        Subtask task = new Subtask("Test 2", "Testing Subtask 2",
                Duration.ofMinutes(5), LocalDateTime.now());
        task.setEpicId(epicId);
        // конвертируем её в JSON
        int initialId = ServerSettings.manager.createSubTask(task);

        assertEquals(1, ServerSettings.manager.getSubTasks().size());

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + initialId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertEquals(0, ServerSettings.manager.getTasks().size());
    }
}