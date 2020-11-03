package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTests {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserSuccess(){
        //Stub Encoder
        when(bCryptPasswordEncoder.encode("abcdefgh")).thenReturn("hash");

        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("abcdefgh");
        req.setConfirmPassword("abcdefgh");

        ResponseEntity<User> response = userController.createUser(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals("test", user.getUsername());
        assertEquals("hash", user.getPassword());
        assertEquals(0, user.getId());
    }

    @Test
    public void createUserPasswordTooShort() {

        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("abc");
        req.setConfirmPassword("abc");

        ResponseEntity<User> response = userController.createUser(req);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void getUserByUsername() {
        User user = new User();
        user.setUsername("Test");

        //Stub UserRepository
        when(userRepository.findByUsername("Test")).thenReturn(user);
        when(userRepository.findByUsername("Wrong")).thenReturn(null);

        //Test correct name
        ResponseEntity<User> response = userController.findByUserName("Test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        user = response.getBody();
        assertEquals("Test", user.getUsername());

        //Test wrong name
        response = userController.findByUserName("Wrong");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
