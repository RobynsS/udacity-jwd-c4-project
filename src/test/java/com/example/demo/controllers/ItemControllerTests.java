package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemControllerTests {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemById(){
        Item item = new Item();
        item.setName("Test");

        //Stub ItemRepository
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(item.getName(), response.getBody().getName());
    }

    @Test
    public void getItemByName(){
        Item item = new Item();
        item.setName("Test");

        //Stub ItemRepository
        when(itemRepository.findByName("Test")).thenReturn(Arrays.asList(item));
        when(itemRepository.findByName("Wrong")).thenReturn(null);

        //Test correct name
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(item.getName(), response.getBody().get(0).getName());

        //Test wrong name
        response = itemController.getItemsByName("Wrong");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
