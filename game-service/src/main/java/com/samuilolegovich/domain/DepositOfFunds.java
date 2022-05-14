package com.samuilolegovich.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Table(name = "deposit_of_funds")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
//  ввод средств
public class DepositOfFunds {
    @Id // @ID - Важно чтобы была из библиотеке -> javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "user_id")
    private long playerId;
    @Column(name = "number_of_coins")

    private long numberOfCoins;
    @Column(name = "coin_name")
    private String coinName;
    private String tag;

    // откуда пришли
    @Column(name = "came_from_wallet")
    private String cameFromWallet;
    // куда пришли
    @Column(name = "came_to_wallet")
    private String cameToWallet;

    @CreationTimestamp
    // указываем что поле не обновляемое
    @Column(name = "created_at", updatable = false)
    // указываем как у нас будет сохранятся дата при сериализации
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
