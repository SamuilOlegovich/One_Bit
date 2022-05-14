package com.samuilolegovich.domain;

import com.samuilolegovich.domain.util.MessageHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Table(name = "messages")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id // @ID - Важно чтобы была из библиотеке -> javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String tag;
    private String message;
    @Column(name = "number_of_likes")
    private int numberOfLikes;

    // много сообщений к одному юзеру
    // и сразу выдергиваем автора к каждому сообщению
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private User author;

    @ManyToMany
    @JoinTable(
            name = "messages_likes",
            joinColumns = { @JoinColumn(name = "message_id") },
            inverseJoinColumns = { @JoinColumn(name = "player_id")}
    )

    private Set<User> likes = new HashSet<>();

    public String getPlayerNickName() {
        return MessageHelper.getPlayerUserName(author);
    }
}
