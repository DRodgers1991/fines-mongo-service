package com.rodgers.fines.data.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Club {

    @Id
    private String id;
    private String clubName;
    private String adminId;
    private List<String> memberIds;

    public Club() {
        //no-op - for Spring
    }

    public Club(String clubName, String adminId) {
        this.adminId = adminId;
        this.clubName = clubName;
        memberIds = new ArrayList<>();
        memberIds.add(adminId);
    }
}
