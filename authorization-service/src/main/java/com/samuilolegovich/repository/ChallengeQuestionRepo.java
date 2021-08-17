package com.samuilolegovich.repository;

import com.samuilolegovich.domain.ChallengeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeQuestionRepo extends JpaRepository<ChallengeQuestion, Long> {

    @Query(value = "select * from challenge_question as ch where ch.user_id = :userId order by RAND() limit :size", nativeQuery = true)
    List<ChallengeQuestion> findByUserId(@Param("userId") Long userId, @Param("size") int size);
}
