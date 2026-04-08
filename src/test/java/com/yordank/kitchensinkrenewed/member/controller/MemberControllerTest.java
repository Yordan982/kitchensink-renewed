package com.yordank.kitchensinkrenewed.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yordank.kitchensinkrenewed.exception.GlobalExceptionHandler;
import com.yordank.kitchensinkrenewed.exception.ResourceNotFoundException;
import com.yordank.kitchensinkrenewed.member.dto.MemberRequest;
import com.yordank.kitchensinkrenewed.member.dto.MemberResponse;
import com.yordank.kitchensinkrenewed.member.dto.PaginatedResponse;
import com.yordank.kitchensinkrenewed.member.repository.MemberRepository;
import com.yordank.kitchensinkrenewed.member.service.MemberService;
import com.yordank.kitchensinkrenewed.member.validation.UniqueEmailValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@Import({GlobalExceptionHandler.class, ValidationAutoConfiguration.class, UniqueEmailValidator.class})
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService service;

    @MockitoBean
    private MemberRepository memberRepository;

    @Test
    @DisplayName("POST /api/members should return 201 and created member")
    void create_shouldReturnCreatedMember() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("1234567890");

        MemberResponse response = new MemberResponse(
                "abc123",
                "John Doe",
                "john@example.com",
                "1234567890"
        );

        when(service.create(any(MemberRequest.class))).thenReturn(response);
        when(memberRepository.existsByEmail("john@example.com")).thenReturn(false);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/members/abc123")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"));
    }

    @Test
    @DisplayName("POST /api/members should return 400 for invalid payload")
    void create_shouldReturnBadRequestForValidationErrors() throws Exception {
        String invalidJson = """
                {
                  "name": "",
                  "email": "not-an-email",
                  "phoneNumber": "123"
                }
                """;

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").value("email must be valid"))
                .andExpect(jsonPath("$.errors.phoneNumber").value("phone number must be 10-12 digits"));
    }

    @Test
    @DisplayName("POST /api/members should return 400 when email already exists")
    void create_shouldReturnBadRequestForDuplicateEmail() throws Exception {
        String json = """
                {
                  "name": "John Doe",
                  "email": "john@example.com",
                  "phoneNumber": "1234567890"
                }
                """;

        when(memberRepository.existsByEmail("john@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("email is already used"));
    }

    @Test
    @DisplayName("GET /api/members should return paginated members")
    void getAll_shouldReturnPaginatedMembers() throws Exception {
        PaginatedResponse<MemberResponse> response = new PaginatedResponse<>(
                List.of(
                        new MemberResponse("1", "Alice", "alice@example.com", "1234567890"),
                        new MemberResponse("2", "Bob", "bob@example.com", "0987654321")
                ),
                1,
                5,
                1,
                2,
                false,
                false
        );

        when(service.getMembers(eq("Ali"), eq(1), eq(5))).thenReturn(response);

        mockMvc.perform(get("/api/members")
                        .param("page", "1")
                        .param("size", "5")
                        .param("search", "Ali"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Alice"))
                .andExpect(jsonPath("$.content[1].name").value("Bob"));
    }

    @Test
    @DisplayName("GET /api/members/{id} should return member")
    void getById_shouldReturnMember() throws Exception {
        MemberResponse response = new MemberResponse(
                "abc123",
                "John Doe",
                "john@example.com",
                "1234567890"
        );

        when(service.getById("abc123")).thenReturn(response);

        mockMvc.perform(get("/api/members/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("abc123"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"));
    }

    @Test
    @DisplayName("GET /api/members/{id} should return 404 when member is missing")
    void getById_shouldReturnNotFound() throws Exception {
        when(service.getById("missing"))
                .thenThrow(new ResourceNotFoundException("Member with id missing not found"));

        mockMvc.perform(get("/api/members/missing"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Member with id missing not found"));
    }
}