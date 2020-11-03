package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		String logOrderSubmitSuccessMessage = String.format("{\"EventDate\":\"%s\", \"EventType\": \"OrderSubmission\", \"EventMsg\":\"Success\"}", new Date().toString());
		String logOrderSubmitFailureMessage = String.format("{\"EventDate\":\"%s\", \"EventType\": \"OrderSubmission\", \"EventMsg\":\"Failure\"}", new Date().toString());

		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.info(logOrderSubmitFailureMessage);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		log.info(logOrderSubmitSuccessMessage);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		String ExceptionMessage = String.format("{\"EventDate\":\"%s\", \"EventType\": \"Exception\", \"EventMsg\":\"Exception\"}", new Date().toString());
		if(user == null) {
			log.info(ExceptionMessage);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
