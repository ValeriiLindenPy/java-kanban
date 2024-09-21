package service;

import org.junit.jupiter.api.*;
import service.interfaces.TaskManagerTest;
import service.managersImpl.InMemoryTaskManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}