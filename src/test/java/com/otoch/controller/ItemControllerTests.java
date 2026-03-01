package com.otoch.controller;

import com.otoch.model.Item;
import com.otoch.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void getAllItems_returnsItemsList() throws Exception {
        Item item = new Item(1L, "Test Item", "Description", 10.0);
        when(itemService.getAllItems()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }

    @Test
    void getItemById_existingId_returnsItem() throws Exception {
        Item item = new Item(1L, "Test Item", "Description", 10.0);
        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void getItemById_nonExistingId_returnsNotFound() throws Exception {
        when(itemService.getItemById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createItem_validItem_returnsCreated() throws Exception {
        Item item = new Item(1L, "New Item", "Description", 25.0);
        when(itemService.createItem(any(Item.class))).thenReturn(item);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Item\", \"description\": \"Description\", \"price\": 25.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Item"));
    }
}
