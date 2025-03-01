package model;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void testEquals() {
        Subtask subtask1 = new Subtask("Test Subtask", "This is a test subtask", 1);
        Subtask subtask2 = new Subtask("Another Subtask", "This is another test subtask", 1);
        Subtask subtask3 = new Subtask("Different Subtask", "This is a different test subtask", 2);

        assertEquals(subtask1, subtask2);
        assertNotEquals(subtask1, subtask3);
    }

    @Test
    void testHashCode() {
        Subtask subtask1 = new Subtask("Test Subtask", "This is a test subtask", 1);
        Subtask subtask2 = new Subtask("Another Subtask", "This is another test subtask", 1);
        Subtask subtask3 = new Subtask("Different Subtask", "This is a different test subtask", 2);

        assertEquals(subtask1.hashCode(), subtask2.hashCode());
        assertNotEquals(subtask1.hashCode(), subtask3.hashCode());
    }


}