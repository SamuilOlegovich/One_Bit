package com.samuilolegovich.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Table(name = "condition")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
    @Id // @ID - Важно чтобы была из библиотеке -> javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int id;
    private long bet;
    // доп смещении для генератора
    private int bias;
}
