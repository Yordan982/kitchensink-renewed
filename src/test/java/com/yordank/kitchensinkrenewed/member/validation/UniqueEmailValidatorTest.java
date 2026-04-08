package com.yordank.kitchensinkrenewed.member.validation;

import com.yordank.kitchensinkrenewed.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UniqueEmailValidatorTest {

    @Test
    void isValid_should_return_true_when_email_is_null() {
        MemberRepository repo = mock(MemberRepository.class);
        UniqueEmailValidator validator = new UniqueEmailValidator(repo);

        boolean result = validator.isValid(null, null);

        assertTrue(result);
        verifyNoInteractions(repo);
    }

    @Test
    void isValid_should_return_true_when_email_is_blank() {
        MemberRepository repo = mock(MemberRepository.class);
        UniqueEmailValidator validator = new UniqueEmailValidator(repo);

        boolean result = validator.isValid("   ", null);

        assertTrue(result);
        verifyNoInteractions(repo);
    }

    @Test
    void isValid_should_return_true_when_email_does_not_exist() {
        MemberRepository repo = mock(MemberRepository.class);
        UniqueEmailValidator validator = new UniqueEmailValidator(repo);

        when(repo.existsByEmail("john@example.com")).thenReturn(false);

        boolean result = validator.isValid("  John@Example.com  ", null);

        assertTrue(result);
        verify(repo).existsByEmail("john@example.com");
    }

    @Test
    void isValid_should_return_false_when_email_already_exists() {
        MemberRepository repo = mock(MemberRepository.class);
        UniqueEmailValidator validator = new UniqueEmailValidator(repo);

        when(repo.existsByEmail("john@example.com")).thenReturn(true);

        boolean result = validator.isValid("John@Example.com", null);

        assertFalse(result);
        verify(repo).existsByEmail("john@example.com");
    }
}