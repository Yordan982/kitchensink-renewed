package com.yordank.kitchensinkrenewed.member.service;

import com.yordank.kitchensinkrenewed.exception.ResourceNotFoundException;
import com.yordank.kitchensinkrenewed.member.dto.MemberRequest;
import com.yordank.kitchensinkrenewed.member.dto.MemberResponse;
import com.yordank.kitchensinkrenewed.member.dto.PaginatedResponse;
import com.yordank.kitchensinkrenewed.member.model.Member;
import com.yordank.kitchensinkrenewed.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository repo;

    @InjectMocks
    private MemberService service;

    @Test
    void create_should_save_and_return_member_response() {
        MemberRequest request = new MemberRequest();
        request.setName("John Doe");
        request.setEmail("John@Email.com ");
        request.setPhoneNumber("1234567890");

        Member saved = new Member("abc123", "John Doe", "john@email.com", "1234567890");

        when(repo.save(any(Member.class))).thenReturn(saved);

        MemberResponse result = service.create(request);

        assertNotNull(result);
        assertEquals("abc123", result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@email.com", result.getEmail());
        assertEquals("1234567890", result.getPhoneNumber());

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(repo).save(memberCaptor.capture());

        Member captured = memberCaptor.getValue();
        assertEquals("John Doe", captured.getName());
        assertEquals("john@email.com", captured.getEmail());
        assertEquals("1234567890", captured.getPhoneNumber());
    }

    @Test
    void getMembers_without_search_should_use_defaults_and_return_paginated_response() {
        Member member = new Member("1", "Alice", "alice@example.com", "1234567890");
        Page<Member> memberPage = new PageImpl<>(List.of(member), PageRequest.of(0, 5), 1);

        when(repo.findAll(any(Pageable.class))).thenReturn(memberPage);

        PaginatedResponse<MemberResponse> result = service.getMembers(null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalElements());
        assertFalse(result.isHasNext());
        assertFalse(result.isHasPrevious());
        assertEquals(1, result.getContent().size());
        assertEquals("Alice", result.getContent().getFirst().getName());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findAll(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());

        Sort.Order nameOrder = pageable.getSort().getOrderFor("name");
        Sort.Order emailOrder = pageable.getSort().getOrderFor("email");

        assertNotNull(nameOrder);
        assertNotNull(emailOrder);
        assertEquals(Sort.Direction.ASC, nameOrder.getDirection());
        assertEquals(Sort.Direction.ASC, emailOrder.getDirection());
    }

    @Test
    void getMembers_with_invalid_page_and_size_should_limit_values() {
        when(repo.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 5), 0));

        service.getMembers(null, 0, 999);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repo).findAll(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(50, pageable.getPageSize());
    }

    @Test
    void getMembers_with_search_should_trim_and_call_searchRepository_method() {
        Member member = new Member("1", "Bob", "bob@example.com", "1234567890");
        Page<Member> memberPage = new PageImpl<>(List.of(member), PageRequest.of(0, 5), 1);

        when(repo.searchMembers(eq(Pattern.quote("Bob")), any(Pageable.class))).thenReturn(memberPage);

        PaginatedResponse<MemberResponse> result = service.getMembers("  Bob  ", 1, 5);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Bob", result.getContent().getFirst().getName());

        verify(repo).searchMembers(eq(Pattern.quote("Bob")), any(Pageable.class));
        verify(repo, never()).findAll(any(Pageable.class));
    }

    @Test
    void getById_should_return_member_response() {
        Member member = new Member("1", "Charlie", "charlie@example.com", "1234567890");

        when(repo.findById("1")).thenReturn(Optional.of(member));

        MemberResponse result = service.getById("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Charlie", result.getName());
        assertEquals("charlie@example.com", result.getEmail());
    }

    @Test
    void getById_when_missing_should_throw_ResourceNotFoundException() {
        when(repo.findById("missing")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getById("missing")
        );

        assertEquals("Member with id missing not found", ex.getMessage());
    }
}