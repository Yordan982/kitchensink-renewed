package com.yordank.kitchensinkrenewed.member.dto;

import com.yordank.kitchensinkrenewed.member.model.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.phoneNumber = member.getPhoneNumber();
    }
}