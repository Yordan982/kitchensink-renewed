package com.yordank.kitchensinkrenewed.member.model;

import com.yordank.kitchensinkrenewed.member.dto.MemberRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String phoneNumber;

    public Member(MemberRequest request) {
        this.name = request.getName();
        this.email = request.getEmail();
        this.phoneNumber = request.getPhoneNumber();
    }
}