package com.rodgers.data.controllers;

import com.rodgers.data.repository.ClubRepository;
import com.rodgers.data.vo.Club;
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
class ClubEntityControllerTests {

    @Mock
    ClubRepository clubRepository;

    @InjectMocks
    ClubEntityController controller = new ClubEntityController();

    @Test
    public void testFindAllEmptyList() {
        when(clubRepository.findAll()).thenReturn(new ArrayList<>());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    public void testFindAllNonEmptyClubList() {
        ArrayList<Club> clubs = new ArrayList<>(Arrays.asList(new Club("North Down","Darren Rodgers"),new Club("Big girls Bingo","Hollie Rodgers")));
        when(clubRepository.findAll()).thenReturn(clubs);
        assertEquals(2, controller.findAll().size());
        assertEquals("Big girls Bingo", controller.findAll().get(1).getClubName());
    }

    @Test
    public void testFindByClubNameNotFound() {
        when(clubRepository.findByClubName("North Down")).thenReturn(new Club("North Down","Darren Rodgers"));
        when(clubRepository.findByClubName("Big girls Bingo")).thenReturn(new Club("Big girls Bingo","Hollie Rodgers"));

        assertNull(controller.findByClubName("Not a real club"));
    }

    @Test
    public void testFindByClubNameFound() {
        when(clubRepository.findByClubName("North Down")).thenReturn(new Club("North Down","Darren Rodgers"));
        when(clubRepository.findByClubName("Big girls Bingo")).thenReturn(new Club("Big girls Bingo","Hollie Rodgers"));

        assertNotNull(controller.findByClubName("North Down"));
    }

    @Test
    public void testFindByIdNotFound() {
        when(clubRepository.findById("1")).thenReturn(Optional.of(new Club("North Down","Darren Rodgers")));
        when(clubRepository.findById("2")).thenReturn(Optional.of(new Club("Big girls Bingo","Hollie Rodgers")));

        assertNull(controller.findById("3"));
    }

    @Test
    public void testFindByIdFound() {
        when(clubRepository.findById("1")).thenReturn(Optional.of(new Club("North Down","Darren Rodgers")));
        when(clubRepository.findById("2")).thenReturn(Optional.of(new Club("Big girls Bingo","Hollie Rodgers")));

        assertNotNull(controller.findById("1"));
    }

    @Test
    public void testNewClubIdAlreadyExists() {
        Club club = Mockito.mock(Club.class);
        when(club.getId()).thenReturn("1");
        when(club.getClubName()).thenReturn("North Down");
        when(clubRepository.findById("1")).thenReturn(Optional.of(club));

        ResponseEntity<String> resp = controller.addClub(club);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Club id Already exists\"}", resp.getBody());
    }

    @Test
    public void testNewClubClubNameAlreadyExists() {
        Club club = Mockito.mock(Club.class);
        when(club.getId()).thenReturn("1");
        when(club.getClubName()).thenReturn("North Down");
        when(clubRepository.findByClubName("North Down")).thenReturn(club);

        ResponseEntity<String> resp = controller.addClub(club);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Club id Already exists\"}", resp.getBody());
    }

    @Test
    public void testNewClubThrowsException() {
        Club club = Mockito.mock(Club.class);
        when(club.getId()).thenReturn("1");
        when(club.getClubName()).thenReturn("North Down");
        doThrow(NullPointerException.class).when(clubRepository).save(club);

        ResponseEntity<String> resp = controller.addClub(club);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Issue while attempting addition of club\"}", resp.getBody());
    }

    @Test
    public void testNewClubHappyPath() {
        Club club = Mockito.mock(Club.class);
        when(club.getId()).thenReturn("1");
        when(club.getClubName()).thenReturn("North Down");

        ResponseEntity<String> resp = controller.addClub(club);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Club addition was a success\"}", resp.getBody());
    }

    @Test
    public void testUpdateIdIsNull() {
        Club club = Mockito.mock(Club.class);
        when(club.getClubName()).thenReturn("North Down");
        when(clubRepository.findById("1")).thenReturn(Optional.of(club));

        ResponseEntity<String> resp = controller.updateClub(club);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Club Id does not exist\"}", resp.getBody());
    }

    @Test
    public void testUpdateIdIsNotFound() {
        Club club = Mockito.mock(Club.class);
        when(club.getId()).thenReturn("1");
        when(club.getClubName()).thenReturn("North Down");

        ResponseEntity<String> resp = controller.updateClub(club);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Club Id does not exist\"}", resp.getBody());
    }

    @Test
    public void testUpdateClubHappyPath() {
        Club club = Mockito.mock(Club.class);
        when(club.getId()).thenReturn("1");
        when(club.getClubName()).thenReturn("North Down");
        when(clubRepository.findById("1")).thenReturn(Optional.of(club));

        ResponseEntity<String> resp = controller.updateClub(club);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Club update was a success\"}", resp.getBody());
    }

    @Test
    public void testDeleteClubIsNotFound() {
        Club club = Mockito.mock(Club.class);
        when(club.getId()).thenReturn("1");
        when(club.getClubName()).thenReturn("North Down");

        ResponseEntity<String> resp = controller.removeClub("1");
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Club Id does not exist\"}", resp.getBody());
    }

    @Test
    public void testDeleteClubThrowsException() {
        Club club = Mockito.mock(Club.class);
        when(club.getId()).thenReturn("1");
        when(club.getClubName()).thenReturn("North Down");
        when(clubRepository.findById("1")).thenReturn(Optional.of(club));
        doThrow(NullPointerException.class).when(clubRepository).delete(club);

        ResponseEntity<String> resp = controller.removeClub("1");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Issue while deleting club\"}", resp.getBody());
    }

    @Test
    public void testDeleteHappyPath() {
        Club club = Mockito.mock(Club.class);
        when(club.getId()).thenReturn("1");
        when(club.getClubName()).thenReturn("North Down");
        when(clubRepository.findById("1")).thenReturn(Optional.of(club));

        ResponseEntity<String> resp = controller.removeClub("1");
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("{\"msg\" : \"Club deleted successfully\"}", resp.getBody());
    }

}
