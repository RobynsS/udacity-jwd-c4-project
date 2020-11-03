package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartControllerTests {

    private CartController cartController;

    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
    }

    @Test
    public void addToCart(){
        //Stub UserRepository
        User user = new User();
        user.setUsername("CorrectName");
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        when(userRepository.findByUsername("CorrectName")).thenReturn(user);
        when(userRepository.findByUsername("WrongName")).thenReturn(null);

        //Stub ItemRepository
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setPrice(BigDecimal.valueOf(1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        //Test correct name and item
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("CorrectName");
        req.setItemId(1L);
        req.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addTocart(req);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        cart = response.getBody();
        assertEquals("CorrectName", cart.getUser().getUsername());
        assertEquals(2, cart.getItems().size());

        //Test wrong item
        req.setItemId(2L);
        response = cartController.addTocart(req);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        //Test wrong user
        req.setUsername("WrongName");
        req.setItemId(1L);
        response = cartController.addTocart(req);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCart(){
        //Stub UserRepository
        User user = new User();
        user.setUsername("CorrectName");
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        when(userRepository.findByUsername("CorrectName")).thenReturn(user);
        when(userRepository.findByUsername("WrongName")).thenReturn(null);

        //Stub ItemRepository
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setPrice(BigDecimal.valueOf(1));
        cart.addItem(item);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        //Test correct name and item
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("CorrectName");
        req.setItemId(1L);
        req.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(req);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        cart = response.getBody();
        assertEquals("CorrectName", cart.getUser().getUsername());
        assertEquals(0, cart.getItems().size());

        //Test wrong item
        req.setItemId(2L);
        response = cartController.removeFromcart(req);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        //Test wrong user
        req.setUsername("WrongName");
        req.setItemId(1L);
        response = cartController.removeFromcart(req);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
