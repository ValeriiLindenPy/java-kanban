package api;

import api.hendlers.typeTokens.TasksListTypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import service.utils.customExceptions.IntersectionTaskException;

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
class HttpTaskServerTest {
    private HttpTaskServer server;
    protected Task task1;
    protected Task task2;
    protected Subtask subtask1;
    protected Subtask subtask2;
    protected Epic epic;

    @BeforeEach
    void setInitState() throws IOException {
        server = new HttpTaskServer();
        LocalDateTime startTime =
                LocalDateTime.of(2024, 6, 11, 14, 9);
        Duration duration = Duration.ofMinutes(20);

        task1 = new Task("Task 1", "Description 1", duration, startTime);
        task2 = new Task("Task 2", "Description 2", duration.plusHours(1), startTime.plusHours(1).plusDays(1));
        epic = new Epic("Epic 1", "Epic Description 1");
        subtask1 = new Subtask("Subtask 1", "Subtask Description 1", duration.plusHours(2), startTime.plusHours(2));
        subtask2 = new Subtask("Subtask 2", "Subtask Description 2", duration.plusHours(3), startTime.plusHours(3).plusDays(1));
        server.start();
    }

    @AfterEach
    void stopServer() throws IOException {
        ServerSettings.manager.deleteTasks();
        ServerSettings.manager.deleteSubTasks();
        ServerSettings.manager.deleteEpics();
        ServerSettings.manager.resetTaskCounter();
        server.stop();
    }

    @Test
    void getHistory () throws IntersectionTaskException, IOException, InterruptedException {
        ServerSettings.manager.clearHistory();
        assertTrue(ServerSettings.manager.getHistory().isEmpty(), "История не пуста перед тестом");

        // конвертируем её в JSON
        int taskId = ServerSettings.manager.createTask(task1);
        ServerSettings.manager.getTaskByID(taskId);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем

        List<Task> parsedTask = ServerSettings.gson.fromJson(response.body(),
                new TasksListTypeToken().getType());

        assertEquals(1, parsedTask.size(), "Некорректное количество задач");
        assertEquals("Task 1", parsedTask.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void getPrioritizedTasks () throws IntersectionTaskException, IOException, InterruptedException {
        // конвертируем её в JSON
        ServerSettings.manager.createTask(task1);
        ServerSettings.manager.createTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем

        List<Task> parsedTask = ServerSettings.gson.fromJson(response.body(),
                new TasksListTypeToken().getType());

        assertEquals(2, parsedTask.size(), "Некорректное количество задач");
        assertEquals("Task 1", parsedTask.get(0).getName(), "Некорректное имя задачи");
        assertEquals("Task 2", parsedTask.get(1).getName(), "Некорректное имя задачи");
    }
}