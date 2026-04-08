package com.yordank.kitchensinkrenewed.member.dto;

import com.yordank.kitchensinkrenewed.member.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberRequest {

    @NotBlank(message = "name is required")
    @Size(min = 1, max = 25, message = "name must be 1-25 characters")
    @Pattern(regexp = "[^0-9]*", message = "name must not contain numbers")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @UniqueEmail
    private String email;

    @NotBlank(message = "phone number is required")
    @Pattern(regexp = "\\d{10,12}", message = "phone number must be 10-12 digits")
    private String phoneNumber;

    public void setEmail(String email) {
        this.email = email == null ? null : email.toLowerCase().trim();
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }
}