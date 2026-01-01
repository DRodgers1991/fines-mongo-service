package com.rodgers.fines.data.controllers;

import com.rodgers.fines.data.repository.UserRepository;
import com.rodgers.fines.data.vo.LoginRequest;
import com.rodgers.fines.data.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "users")
@Slf4j
public class UserEntityController {

    @Autowired
    private UserRepository userRepository;
    private final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @GetMapping("validLogin")
    public ResponseEntity<String> validLogin(@RequestBody() LoginRequest request) {
        User user = userRepository.findByUserName(request.getUsername());
        if(user != null) {
            if(ENCODER.matches(request.getPassword(), user.getPassword())) {
                if(log.isDebugEnabled()) {
                    log.debug("Valid Login attempt for {} ",user.getUserName());
                }
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        if(log.isDebugEnabled()) {
            log.debug("Invalid Login attempt for {} ",request.getUsername());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("findByUserName")
    public User findByUserName(@RequestParam("user") String userName) {
        return userRepository.findByUserName(userName);
    }

    @GetMapping("findById")
    public User findById(@RequestParam("id") String id) {
        return userRepository.findById(id).orElse(null);
    }

    @PutMapping("addUser")
    public ResponseEntity<String> addUser(@RequestBody() User user) {
        if(doesUserExist(user)) {
            log.error("Attempting to save a new user with existing Id or username Rejecting {} ", user);
            return new ResponseEntity<>("{\"msg\" : \"User id Already exists\"}", HttpStatus.BAD_REQUEST);
        }
        return saveUser(user, "addition");
    }

    @PatchMapping("updateUser")
    public ResponseEntity<String> updateUser(@RequestBody() User user) {
        if(!doesUserExist(user)) {
            return userNotFoundStatus(user);
        }
        return saveUser(user, "update");
    }

    @DeleteMapping("removeUser")
    public ResponseEntity<String> removeUser(@RequestParam("id") String id) {
        User user = findById(id);
        if(user == null) {
            return userNotFoundStatus(null);
        } else {
            try {
                userRepository.delete(user);
            } catch (Exception e) {
                log.error("Could not remove existing user | {}",e.getMessage());
                return new ResponseEntity<>("{\"msg\" : \"Issue while deleting user\"}", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        log.info("User deleted successfully | {}",user);
        return new ResponseEntity<>("{\"msg\" : \"User deleted successfully\"}", HttpStatus.OK);
    }

    private boolean doesUserExist(User user) {
        if(user.getId() != null && findById(user.getId()) != null) {
            return true;
        } else {
            return user.getUserName() != null && findByUserName(user.getUserName()) != null;
        }
    }

    private ResponseEntity<String> userNotFoundStatus(User user) {
        log.error("Id is null or user not found {} ", user);
        return new ResponseEntity<>("{\"msg\" : \"User Id does not exist\"}", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> saveUser(User user, String action) {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Could not {} existing user | {}",action,e.getMessage());
            return new ResponseEntity<>(String.format("{\"msg\" : \"Issue while attempting %s of user\"}",action), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("User {} was a success | {}",action, user);
        return new ResponseEntity<>(String.format("{\"msg\" : \"User %s was a success\"}",action), HttpStatus.OK);
    }
}
