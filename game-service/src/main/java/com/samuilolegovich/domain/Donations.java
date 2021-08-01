package com.samuilolegovich.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.samuilolegovich.enums.Prize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private long totalDonations;
    private Prize typeWin;
    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
}
