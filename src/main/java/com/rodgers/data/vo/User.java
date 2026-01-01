package com.rodgers.data.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {

    @Id
    private String id;
    private String userName;
    private String password;

    public User(String userName) {
        this.userName = userName;
    }
}
