package com.rodgers.data;

import com.rodgers.data.repository.UserRepository;
import com.rodgers.data.vo.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserEntityControllerTests {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserEntityController controller = new UserEntityController();

    @Test
    public void testFindAllEmptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    public void testFindAllNonEmptyUserList() {
        ArrayList<User> users = new ArrayList<>(Arrays.asList(new User("Darren Rodgers"),new User("Hollie Rodgers")));
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(2, controller.findAll().size());
        assertEquals("Hollie Rodgers", controller.findAll().get(1).getUserName());
    }

    @Test
    public void testFindByUserNameNotFound() {
        when(userRepository.findByUserName("Darren Rodgers")).thenReturn(new User("Darren Rodgers"));
        when(userRepository.findByUserName("Hollie Rodgers")).thenReturn(new User("Hollie Rodgers"));

        assertNull(controller.findByUserName("Lucifer Morningstar"));
    }

    @Test
    public void testFindByUserNameFound() {
        when(userRepository.findByUserName("Darren Rodgers")).thenReturn(new User("Darren Rodgers"));
        when(userRepository.findByUserName("Hollie Rodgers")).thenReturn(new User("Hollie Rodgers"));

        assertNotNull(controller.findByUserName("Darren Rodgers"));
    }

    @Test
    public void testFindByIdNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(new User("Darren Rodgers")));
        when(userRepository.findById("2")).thenReturn(Optional.of(new User("Hollie Rodgers")));

        assertNull(controller.findById("3"));
    }

    @Test
    public void testFindByIdFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(new User("Darren Rodgers")));
        when(userRepository.findById("2")).thenReturn(Optional.of(new User("Hollie Rodgers")));

        assertNotNull(controller.findById("1"));
    }

    @Test
    public void testNewUserIdAlreadyExists() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("Darren Rodgers");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        ResponseEntity<String> resp = controller.addUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("{\"msg\" : \"User id Already exists\"}", resp.getBody());
    }

    @Test
    public void testNewUserUserNameAlreadyExists() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(null);
        when(user.getUserName()).thenReturn("Darren Rodgers");
        when(userRepository.findByUserName("Darren Rodgers")).thenReturn(user);

        ResponseEntity<String> resp = controller.addUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("{\"msg\" : \"User id Already exists\"}", resp.getBody());
    }

    @Test
    public void testNewUserThrowsException() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("Darren Rodgers");
        doThrow(NullPointerException.class).when(userRepository).save(user);

        ResponseEntity<String> resp = controller.addUser(user);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Issue while attempting addition of user\"}", resp.getBody());
    }

    @Test
    public void testNewUserHappyPath() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("Darren Rodgers");

        ResponseEntity<String> resp = controller.addUser(user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("{\"msg\" : \"User addition was a success\"}", resp.getBody());
    }

    @Test
    public void testUpdateIdIsNull() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(null);
        when(user.getUserName()).thenReturn("Darren Rodgers");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        ResponseEntity<String> resp = controller.updateUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("{\"msg\" : \"User Id does not exist\"}", resp.getBody());
    }

    @Test
    public void testUpdateIdIsNotFound() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("Darren Rodgers");

        ResponseEntity<String> resp = controller.updateUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("{\"msg\" : \"User Id does not exist\"}", resp.getBody());
    }

    @Test
    public void testUpdateUserHappyPath() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("Darren Rodgers");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        ResponseEntity<String> resp = controller.updateUser(user);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("{\"msg\" : \"User update was a success\"}", resp.getBody());
    }

    @Test
    public void testDeleteUserIsNotFound() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(null);
        when(user.getUserName()).thenReturn("Darren Rodgers");

        ResponseEntity<String> resp = controller.removeUser("1");
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("{\"msg\" : \"User Id does not exist\"}", resp.getBody());
    }

    @Test
    public void testDeleteUserThrowsException() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("Darren Rodgers");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        doThrow(NullPointerException.class).when(userRepository).delete(user);

        ResponseEntity<String> resp = controller.removeUser("1");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Issue while deleting user\"}", resp.getBody());
    }

    @Test
    public void testDeleteHappyPath() {
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn("1");
        when(user.getUserName()).thenReturn("Darren Rodgers");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        ResponseEntity<String> resp = controller.removeUser("1");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("{\"msg\" : \"User deleted successfully\"}", resp.getBody());
    }

}
