package com.yordank.kitchensinkrenewed.member.controller;

import com.yordank.kitchensinkrenewed.member.dto.MemberRequest;
import com.yordank.kitchensinkrenewed.member.dto.MemberResponse;
import com.yordank.kitchensinkrenewed.member.dto.PaginatedResponse;
import com.yordank.kitchensinkrenewed.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @PostMapping
    public ResponseEntity<MemberResponse> create(@Valid @RequestBody MemberRequest request) {
        MemberResponse created = service.create(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public PaginatedResponse<MemberResponse> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String search
    ) {
        return service.getMembers(search, page, size);
    }

    @GetMapping("/{id}")
    public MemberResponse getById(@PathVariable String id) {
        return service.getById(id);
    }
}