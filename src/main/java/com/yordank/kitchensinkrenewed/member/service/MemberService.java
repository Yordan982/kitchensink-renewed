package com.yordank.kitchensinkrenewed.member.service;

import com.yordank.kitchensinkrenewed.exception.ResourceNotFoundException;
import com.yordank.kitchensinkrenewed.member.dto.MemberRequest;
import com.yordank.kitchensinkrenewed.member.dto.MemberResponse;
import com.yordank.kitchensinkrenewed.member.dto.PaginatedResponse;
import com.yordank.kitchensinkrenewed.member.model.Member;
import com.yordank.kitchensinkrenewed.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository repo;

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 5;
    private static final int MAX_SIZE = 50;

    public MemberResponse create(MemberRequest request) {
        log.info("Creating a new member");
        Member member = new Member(request);
        return new MemberResponse(repo.save(member));
    }

    public PaginatedResponse<MemberResponse> getMembers(String search, Integer page, Integer size) {
        int actualPage = (page == null || page < 1) ? DEFAULT_PAGE : page;
        int actualSize = (size == null || size < 1) ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);

        Pageable pageable = PageRequest.of(
                actualPage - 1,
                actualSize,
                Sort.by(
                        Sort.Order.asc("name"),
                        Sort.Order.asc("email")
                )
        );

        Page<Member> memberPage = StringUtils.hasText(search)
                ? repo.searchMembers(Pattern.quote(search.trim()), pageable)
                : repo.findAll(pageable);

        List<MemberResponse> content = memberPage.map(MemberResponse::new).getContent();

        return new PaginatedResponse<>(
                content,
                memberPage.getNumber() + 1,
                memberPage.getSize(),
                memberPage.getTotalPages(),
                memberPage.getTotalElements(),
                memberPage.hasNext(),
                memberPage.hasPrevious()
        );
    }

    public MemberResponse getById(String id) {
        log.info("Fetching member by ID {}", id);
        return repo.findById(id)
                .map(MemberResponse::new)
                .orElseThrow(() -> new ResourceNotFoundException("Member with id " + id + " not found"));
    }
}