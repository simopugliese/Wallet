package com.simonepugliese.Manager;

import com.simonepugliese.Criptor.Criptor;
import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.LoginItem;
import com.simonepugliese.Saver.Saver;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    // Mock Class per Criptor: Traccia le chiamate e simula il risultato
    static class MockCriptor extends Criptor {
        public int criptaCallCount = 0;
        public int decriptaCallCount = 0;

        @Override
        public Item cripta(Item item) {
            criptaCallCount++;
            // Simula la crittografia: aggiunge un suffisso al nome
            return new LoginItem(item.getDescription() + "_CRYPTED", "c", "c", "c");
        }

        @Override
        public Item decripta(Item item) {
            decriptaCallCount++;
            // Simula la decrittografia: rimuove il suffisso
            return new LoginItem(item.getDescription().replace("_CRYPTED", ""), "d", "d", "d");
        }
    }

    // Mock Class per Saver: Traccia le chiamate e fornisce dati simulati
    static class MockSaver extends Saver {
        public int salvaCallCount = 0;
        public int caricaCallCount = 0;

        @Override
        public void salva(Item item) {
            salvaCallCount++;
        }

        @Override
        public List<Item> carica() {
            caricaCallCount++;
            // Restituisce un Item che sembra criptato (da decriptare)
            return List.of(new LoginItem("TestItem_CRYPTED", "c", "c", "c"));
        }
    }

    @Test
    void criptaPoiSalva_shouldCallCriptaAndSalvaOnce() {
        MockCriptor mockCriptor = new MockCriptor();
        MockSaver mockSaver = new MockSaver();
        Manager manager = new Manager(mockCriptor, mockSaver);

        Item item = new LoginItem("Test Item", "u", "p", "url");

        // Quando chiamo criptaPoiSalva
        manager.criptaPoiSalva(item);

        // Allora devo aver chiamato Cripta e Salva esattamente una volta
        assertEquals(1, mockCriptor.criptaCallCount);
        assertEquals(1, mockSaver.salvaCallCount);
    }

    @Test
    void caricaPoiDecripta_shouldCallCaricaOnceAndDecriptaForEachItem() {
        MockCriptor mockCriptor = new MockCriptor();
        MockSaver mockSaver = new MockSaver();
        Manager manager = new Manager(mockCriptor, mockSaver);

        // La MockSaver restituisce 1 item

        // Quando chiamo caricaPoiDecripta
        List<Item> items = manager.caricaPoiDecripta();

        // Allora devo aver chiamato Carica una volta
        assertEquals(1, mockSaver.caricaCallCount);
        // E Decripta una volta (per l'unico item restituito dalla MockSaver)
        assertEquals(1, mockCriptor.decriptaCallCount);

        // E l'item decriptato deve essere stato aggiunto alla lista
        assertEquals(1, items.size());
        assertEquals("TestItem", items.get(0).getDescription()); // Verifica la decrittazione simulata
    }
}