package com.rodgers.data.repository;

import com.rodgers.data.vo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface UserRepository extends MongoRepository<User, String> {
    User findByUserName(String userName);
}
