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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    // указываем как у нас будет сохранятся дата при сериализации
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
