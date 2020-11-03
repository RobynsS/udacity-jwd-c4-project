package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerTests {

    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void submitOrder(){
        //Stub userRepository
        User user = new User();
        user.setUsername("CorrectName");
        Cart cart = new Cart();
        Item item = new Item();
        item.setName("Item");
        item.setPrice(BigDecimal.valueOf(1));
        cart.addItem(item);
        user.setCart(cart);

        when(userRepository.findByUsername("CorrectName")).thenReturn(user);
        when(userRepository.findByUsername("WrongName")).thenReturn(null);

        //Stub orderRepository
        UserOrder order = UserOrder.createFromCart(user.getCart());
        when(orderRepository.save(order)).thenReturn(order);

        //Test correct username
        ResponseEntity<UserOrder> response = orderController.submit("CorrectName");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(order.getUser(), response.getBody().getUser());

        //Test wrong username
        response = orderController.submit("WrongName");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrderByUser(){
        //Stub userRepository
        User user = new User();
        user.setUsername("CorrectName");
        Cart cart = new Cart();
        Item item = new Item();
        item.setName("Item");
        item.setPrice(BigDecimal.valueOf(1));
        cart.addItem(item);
        user.setCart(cart);

        when(userRepository.findByUsername("CorrectName")).thenReturn(user);
        when(userRepository.findByUsername("WrongName")).thenReturn(null);

        //Stub orderRepository
        UserOrder order = UserOrder.createFromCart(user.getCart());
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order));

        //Test correct username
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("CorrectName");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(order.getUser(), response.getBody().get(0).getUser());

        //Test wrong username
        response = orderController.getOrdersForUser("WrongName");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
