package com.samuilolegovich.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.samuilolegovich.enums.RedBlack;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Table(name = "statistics_wins_and_losses")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
//  Статистика выигрышей и проигрышей
public class StatisticsWinsAndLosses {
    @Id // @ID - Важно чтобы была из библиотеке -> javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "player_id")
    private long playerId;
    private long bet;
    private long win;

    @Column(name = "type_bet")
    @Enumerated(EnumType.STRING)
    private RedBlack typeBet;

    @Column(name = "type_win")
    @Enumerated(EnumType.STRING)
    private RedBlack typeWin;

    @CreationTimestamp
    // указываем что поле не обновляемое
    @Column(name = "created_at", updatable = false)
    // указываем как у нас будет сохранятся дата при сериализации
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
