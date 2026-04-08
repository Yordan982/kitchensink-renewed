package com.yordank.kitchensinkrenewed.member.repository;

import com.yordank.kitchensinkrenewed.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MemberRepository extends MongoRepository<Member, String> {

    boolean existsByEmail(String email);

    @Query("{ $or: [ " +
            "{ 'name': { $regex: ?0, $options: 'i' } }, " +
            "{ 'email': { $regex: ?0, $options: 'i' } }, " +
            "{ 'phoneNumber': { $regex: ?0, $options: 'i' } } " +
            "] }")
    Page<Member> searchMembers(String keyword, Pageable pageable);
}