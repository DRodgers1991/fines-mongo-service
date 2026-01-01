package com.rodgers.fines.data.controllers;

import com.rodgers.fines.data.repository.ClubRepository;
import com.rodgers.fines.data.vo.Club;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "clubs")
@Slf4j
public class ClubEntityController {

    @Autowired
    private ClubRepository clubRepository;

    @GetMapping("findAll")
    public List<Club> findAll() {
        return clubRepository.findAll();
    }

    @GetMapping("findByClubName")
    public Club findByClubName(@RequestParam("club") String clubName) {
        return clubRepository.findByClubName(clubName);
    }

    @GetMapping("findById")
    public Club findById(@RequestParam("id") String id) {
        return clubRepository.findById(id).orElse(null);
    }

    @PutMapping("addClub")
    public ResponseEntity<String> addClub(@RequestBody() Club club) {
        if(doesClubExist(club)) {
            log.error("Attempting to save a new club with existing Id Rejecting {} ", club);
            return new ResponseEntity<>("{\"msg\" : \"Club id Already exists\"}", HttpStatus.BAD_REQUEST);
        }
        return saveClub(club, "addition");
    }

    @PatchMapping("updateClub")
    public ResponseEntity<String> updateClub(@RequestBody() Club club) {
        if(!doesClubExist(club)) {
            return userNotFoundStatus(club);
        }
        return saveClub(club, "update");
    }

    @DeleteMapping("removeClub")
    public ResponseEntity<String> removeClub(@RequestParam("id") String id) {
        Club club = findById(id);
        if(club == null) {
            return userNotFoundStatus(null);
        } else {
            try {
                clubRepository.delete(club);
            } catch (Exception e) {
                log.error("Could not remove existing club | {}",e.getMessage());
                return new ResponseEntity<>("{\"msg\" : \"Issue while deleting club\"}", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        log.info("Club deleted successfully | {}",club);
        return new ResponseEntity<>("{\"msg\" : \"Club deleted successfully\"}", HttpStatus.OK);
    }

    private boolean doesClubExist(Club club) {
        if(club.getId() != null && findById(club.getId()) != null) {
            return true;
        } else {
            return club.getClubName() != null && findByClubName(club.getClubName()) != null;
        }
    }

    private ResponseEntity<String> userNotFoundStatus(Club club) {
        log.error("Id is null or club not found {} ", club);
        return new ResponseEntity<>("{\"msg\" : \"Club Id does not exist\"}", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> saveClub(Club club, String action) {
        try {
            clubRepository.save(club);
        } catch (Exception e) {
            log.error("Could not {} existing club | {}",action,e.getMessage());
            return new ResponseEntity<>(String.format("{\"msg\" : \"Issue while attempting %s of club\"}",action), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("Club {} was a success | {}",action, club);
        return new ResponseEntity<>(String.format("{\"msg\" : \"Club %s was a success\"}",action), HttpStatus.OK);
    }
}
