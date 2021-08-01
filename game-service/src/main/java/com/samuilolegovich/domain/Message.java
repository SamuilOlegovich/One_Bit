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

    // много сообщений к одному юзеру
    // и сразу выдергиваем автора к каждому сообщению
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player author;

    @ManyToMany
    @JoinTable(
            name = "messages_likes",
            joinColumns = { @JoinColumn(name = "messages_id") },
            inverseJoinColumns = { @JoinColumn(name = "player_id")}
    )
    private Set<Player> likes = new HashSet<>();

    public String getPlayerNickName() {
        return MessageHelper.getPlayerUserName(author);
    }
}
