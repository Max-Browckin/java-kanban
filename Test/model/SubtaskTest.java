package model;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void SubtasksWithEqualEpicIDShouldBeEqual() {
        Subtask subtask1 = new Subtask(10, "Забрать заказ", "На Ozon", Status.NEW, 5);
        Subtask subtask2 = new Subtask(10, "Забрать заказ", "На Wildberries", Status.DONE, 5);
        assertEquals(subtask1, subtask2, "Ошибка! Наследники класса Task должны быть равны друг другу, если равен их epicID;");
    }
}