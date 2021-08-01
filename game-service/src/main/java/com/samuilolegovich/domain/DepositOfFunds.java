package com.samuilolegovich.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private long userId;
    // откуда пришли
    private String cameFromWallet;
    // куда пришли
    private String cameToWallet;
    private long numberOfCoins;
    private String coinName;
    private String tag;
    // указываем что поле не обновляемое
    @Column(updatable = false)
    // указываем как у нас будет сохранятся дата при сериализации
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
}
