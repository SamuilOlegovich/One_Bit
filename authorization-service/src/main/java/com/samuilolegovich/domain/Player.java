package com.samuilolegovich.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Table(name = "players")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Данные о игроке
public class Player {
    @Id // @ID - Важно чтобы была из библиотеке -> javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotBlank(message = "Nickname name cannot be empty")
    private String userName;
    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    private String email;
    @NotBlank(message = "Password cannot be empty")
    private String password;

    private String wallet;
    private String tagWallet;
    private long credits;

    @Enumerated(EnumType.STRING)
    private AccountStatusCode accountStatusCode;
    // можно использовать для верификации емейла, для подтверждения вывода средств, для смены ароля
    private String activationAccountCode;
    private String payCode;
    private String restartPasswordCode;
    private int incorrectLoginCounter;
    /*
        @ElementCollection - позволяет сформировать таблицу для enam
        так же ствим ленивую подгрузку (грузит по мере необходимости)

        @CollectionTable - говорит что данное поле будет хранится в отдельной таблице
        для которой не описывали мепинг

        @Enumerated - указываем в каком формате хотим хранить Enum
    */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "player_role", joinColumns = @JoinColumn(name = "player_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    // это у нас обратная связ по сообщением связана с автором
    // (будем получать все сррбщения которые были созданы пользователем)
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Message> messages;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginTimestamp;
    private LocalDateTime accountBlockTimestamp;
    private LocalDateTime resetTokenTimestamp;
    private LocalDateTime lastRequestTimestamp;

    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }
}
