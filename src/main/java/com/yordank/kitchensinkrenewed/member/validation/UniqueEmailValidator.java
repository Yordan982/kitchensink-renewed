package com.yordank.kitchensinkrenewed.member.validation;

import com.yordank.kitchensinkrenewed.member.repository.MemberRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final MemberRepository memberRepository;

    public UniqueEmailValidator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return !memberRepository.existsByEmail(email.trim().toLowerCase());
    }
}