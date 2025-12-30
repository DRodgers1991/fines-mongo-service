package com.rodgers.data.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
public class User {

    @Id
    private String id;
    private String userName;
    public User() {
        //no-op - for Spring
    }

    public User(String userName) {
        this.userName = userName;
    }
}
