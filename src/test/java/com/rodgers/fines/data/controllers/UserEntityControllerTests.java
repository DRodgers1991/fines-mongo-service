package com.rodgers.fines.data.controllers;

import com.rodgers.fines.data.repository.UserRepository;
import com.rodgers.fines.data.vo.LoginRequest;
import com.rodgers.fines.data.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserEntityControllerTests {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserEntityController controller = new UserEntityController();

    @Test
    public void testFindByUserNameNotFound() {
        when(userRepository.findByUserName("user1")).thenReturn(new User("user1"));
        when(userRepository.findByUserName("user2")).thenReturn(new User("user2"));

        Assertions.assertNull(controller.findByUserName("Lucifer Morningstar"));
    }

    @Test
    public void testFindByUserNameFound() {
        when(userRepository.findByUserName("user1")).thenReturn(new User("user1"));
        when(userRepository.findByUserName("user2")).thenReturn(new User("user2"));

        Assertions.assertNotNull(controller.findByUserName("user1"));
    }

    @Test
    public void testFindByIdNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(new User("user1")));
        when(userRepository.findById("2")).thenReturn(Optional.of(new User("user2")));

        Assertions.assertNull(controller.findById("3"));
    }

    @Test
    public void testFindByIdFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(new User("user1")));
        when(userRepository.findById("2")).thenReturn(Optional.of(new User("user2")));

        Assertions.assertNotNull(controller.findById("1"));
    }

    @Test
    public void testNewUserIdAlreadyExists() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("user1");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        ResponseEntity<String> resp = controller.addUser(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("{\"msg\" : \"User id Already exists\"}", resp.getBody());
    }

    @Test
    public void testNewUserUserNameAlreadyExists() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(null);
        when(user.getUserName()).thenReturn("user1");
        when(userRepository.findByUserName("user1")).thenReturn(user);

        ResponseEntity<String> resp = controller.addUser(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("{\"msg\" : \"User id Already exists\"}", resp.getBody());
    }

    @Test
    public void testNewUserThrowsException() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("user1");
        doThrow(NullPointerException.class).when(userRepository).save(user);

        ResponseEntity<String> resp = controller.addUser(user);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Assertions.assertEquals("{\"msg\" : \"Issue while attempting addition of user\"}", resp.getBody());
    }

    @Test
    public void testNewUserHappyPath() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("user1");

        ResponseEntity<String> resp = controller.addUser(user);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertEquals("{\"msg\" : \"User addition was a success\"}", resp.getBody());
    }

    @Test
    public void testUpdateIdIsNull() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(null);
        when(user.getUserName()).thenReturn("user1");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        ResponseEntity<String> resp = controller.updateUser(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("{\"msg\" : \"User Id does not exist\"}", resp.getBody());
    }

    @Test
    public void testUpdateIdIsNotFound() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("user1");

        ResponseEntity<String> resp = controller.updateUser(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("{\"msg\" : \"User Id does not exist\"}", resp.getBody());
    }

    @Test
    public void testUpdateUserHappyPath() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("user1");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        ResponseEntity<String> resp = controller.updateUser(user);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertEquals("{\"msg\" : \"User update was a success\"}", resp.getBody());
    }

    @Test
    public void testDeleteUserIsNotFound() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(null);
        when(user.getUserName()).thenReturn("user1");

        ResponseEntity<String> resp = controller.removeUser("1");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("{\"msg\" : \"User Id does not exist\"}", resp.getBody());
    }

    @Test
    public void testDeleteUserThrowsException() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("user1");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        doThrow(NullPointerException.class).when(userRepository).delete(user);

        ResponseEntity<String> resp = controller.removeUser("1");
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Assertions.assertEquals("{\"msg\" : \"Issue while deleting user\"}", resp.getBody());
    }

    @Test
    public void testDeleteHappyPath() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("user1");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        ResponseEntity<String> resp = controller.removeUser("1");
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertEquals("{\"msg\" : \"User deleted successfully\"}", resp.getBody());
    }

    @Test
    public void testLoginUserNotFound() {
        when(userRepository.findByUserName("user1")).thenReturn(null);
        LoginRequest login = new LoginRequest();
        login.setPassword("1234");
        login.setUsername("user1");
        ResponseEntity<String> response = controller.validLogin(login);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testLoginPasswordsDontMatch() {
        User user = Mockito.mock(User.class);
        when(user.getPassword()).thenReturn(new BCryptPasswordEncoder().encode("pass1"));
        when(userRepository.findByUserName("user1")).thenReturn(user);
        LoginRequest login = new LoginRequest();
        login.setPassword("pass2");
        login.setUsername("user1");
        ResponseEntity<String> response = controller.validLogin(login);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void passwordsMatch() {
        User user = Mockito.mock(User.class);
        when(user.getPassword()).thenReturn(new BCryptPasswordEncoder().encode("pass2"));
        when(userRepository.findByUserName("user1")).thenReturn(user);
        LoginRequest login = new LoginRequest();
        login.setPassword("pass2");
        login.setUsername("user1");
        ResponseEntity<String> response = controller.validLogin(login);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
