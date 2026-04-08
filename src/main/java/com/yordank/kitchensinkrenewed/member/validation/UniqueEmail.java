package com.yordank.kitchensinkrenewed.member.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented
public @interface UniqueEmail {
    String message() default "email is already used";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}