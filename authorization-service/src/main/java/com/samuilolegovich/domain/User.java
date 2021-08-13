package com.samuilolegovich.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;

@Data
@Table(name = "userss")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Данные о игроке
public class User {
    @Id // @ID - Важно чтобы была из библиотеке -> javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "user_name")
    @NotBlank(message = "Nickname name cannot be empty")
    private String userName;
    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    private String email;
    @NotBlank(message = "Password cannot be empty")
    private String password;

    private boolean active;
    private boolean locked;

    private String wallet;
    private String tagWallet;
    private long credits;

    @Enumerated(EnumType.STRING)
    private AccountStatusCode accountStatusCode;
    // можно использовать для верификации емейла, для подтверждения вывода средств
    private String activationAccountCode;
    private String resetPasswordToken;
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
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = LAZY)
    private Set<Message> messages;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    // указываем как у нас будет сохранятся дата при сериализации
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "last_login_timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTimestamp;

    @Column(name = "account_block_timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accountBlockTimestamp;

    @Column(name = "reset_token_Timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resetTokenTimestamp;

    @Column(name = "last_request_timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastRequestTimestamp;

    @Column(name = "password_timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime passwordTimestamp;

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = {PERSIST, REFRESH, MERGE, REMOVE}, orphanRemoval = true)
    private List<PasswordHistory> passwordHistories = new LinkedList<>();



    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }



    public User setPassword(String encryptPassword) {
        LocalDateTime timestamp = now();
        PasswordHistory history = PasswordHistory.builder().password(password).createdAt(timestamp).user(this).build();
        if (passwordHistories.size() >= 24) {
            passwordHistories.remove(0);
        }
        passwordHistories.add(history);
        this.password = encryptPassword;
        passwordTimestamp = timestamp;
        return this;
    }



    public boolean isOnline() {
        return (nonNull(lastRequestTimestamp)) && ChronoUnit.MINUTES.between(lastRequestTimestamp, now()) < 15;
    }

}
