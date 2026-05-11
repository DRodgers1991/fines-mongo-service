package com.rodgers.fines.data.repository;

import com.rodgers.fines.data.vo.Club;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface ClubRepository extends MongoRepository<Club, String> {
    Club findByClubName(String clubName);
    Club findByAdminId(String adminId);
    List<Club> findByMemberIdIn(List<String> memberIds);
}
