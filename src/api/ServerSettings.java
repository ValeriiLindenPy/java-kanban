package api;

import api.hendlers.adapters.DurationTypeAdapter;
import api.hendlers.adapters.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.Managers;
import service.interfaces.TaskManager;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class ServerSettings {
    //server port settings
    public static final int PORT = 8080;
    //server gson settings
    public static final Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    //server manager settings
    public static TaskManager manager = Managers.getDefault();
    //server datetime format settings
    public static DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    //server charset settings
    public static Charset charset = StandardCharsets.UTF_8;

    public static void setManager(TaskManager manager) {
        ServerSettings.manager = manager;
    }
}
