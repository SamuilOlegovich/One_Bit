package com.samuilolegovich.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.samuilolegovich.enums.Prize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Table(name = "donations")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donations {
    @Id // @ID - Важно чтобы была из библиотеке -> javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long donations;
    private long win;
    @Column(name = "type_win")
    private Prize typeWin;
    @Column(name = "total_donations")
    private long totalDonations;

    @CreationTimestamp
    // указываем что поле не обновляемое
    @Column(name = "created_at", updatable = false)
    // указываем как у нас будет сохранятся дата при сериализации
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
