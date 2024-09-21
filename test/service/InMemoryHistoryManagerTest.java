package service;

import org.junit.jupiter.api.TestInstance;
import service.interfaces.HistoryManager;
import service.interfaces.HistoryManagerTest;
import service.managersImpl.InMemoryHistoryManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InMemoryHistoryManagerTest extends HistoryManagerTest<HistoryManager> {

    @Override
    protected HistoryManager createHistoryManager() {
        return new InMemoryHistoryManager();
    }
}