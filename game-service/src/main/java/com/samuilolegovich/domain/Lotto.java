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
@Table(name = "lotto")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lotto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long replenishment;
    @Column(name = "total_lotto_credits")
    private long totalLottoCredits;

    @CreationTimestamp
    // указываем что поле не обновляемое
    @Column(name = "created_at", updatable = false)
    // указываем как у нас будет сохранятся дата при сериализации
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
