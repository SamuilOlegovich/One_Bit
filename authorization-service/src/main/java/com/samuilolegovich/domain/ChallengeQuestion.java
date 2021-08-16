package com.samuilolegovich.domain;

import com.samuilolegovich.enums.ChallengeQuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeQuestion {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Enumerated(STRING)
    private ChallengeQuestionType type;
    private String answer;
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public String toString() {
        return String.format("ChallengeQuestion {id: %d, type: %s, answer: %s, userId; %d}",
                id, type, answer, user.getId());
    }
}
