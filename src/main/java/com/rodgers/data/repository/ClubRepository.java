package com.rodgers.data.repository;

import com.rodgers.data.vo.Club;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ClubRepository extends MongoRepository<Club, String> {
    Club findByClubName(String clubName);
}
