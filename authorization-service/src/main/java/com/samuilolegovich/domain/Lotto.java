package com.samuilolegovich.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private long lottoCredits;
}
