package com.samuilolegovich.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Table(name = "arsenal")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Данные о состоянии призового фонда и т д (самя актуальное значение на данный момент это последняя строчка)
public class Arsenal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long credits;
}
