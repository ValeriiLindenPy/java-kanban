package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    /**
     * утилитарный класс всегда возвращает проинициализированные
     * и готовые к работе экземпляры менеджеров;
     */

    @Test
    void getDefault() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void getDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory());
    }
}