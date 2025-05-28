/*
package com.example.evoFunServer.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"EvoUser\"")
public class EvoUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"userID\"")
    private Long userID;
    @Column(name = "\"name\"")
    private String name;
    @Column(name = "\"surname\"")
    private String surname;
    @Column(name = "\"nickname\"")
    private String nickname;
    @Column(name = "\"phoneNumber\"")
    private String phoneNumber;
    @Column(name = "\"email\"")
    private String email;
    @Column(name = "\"login\"")
    private String login;
    @Column(name = "\"pass\"")
    private String pass;
    @Column(name = "\"balance\"")
    private int balance;


}
*/


package com.evofun.userservice.common.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
//@AllArgsConstructor
@Entity
@Table(name = "\"EvoUser\"")
public class User {

    @Version
    @Column(name = "\"version\"")
    private long version;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "\"userID\"", updatable = false, nullable = false, unique = true)
    private UUID userID;

    @Column(name = "\"name\"", nullable = false, length = 128)
    private String name;

    @Column(name = "\"surname\"", nullable = false, length = 128)
    private String surname;

    @Column(name = "\"nickname\"", nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "\"phoneNumber\"", nullable = false, length = 30)
    private String phoneNumber;

    @Column(name = "\"email\"", nullable = false, length = 100)
    private String email;

/*    @Column(name = "\"login\"", nullable = false, length = 64)
    private String login;*/

    @Column(name = "\"pass\"", nullable = false, length = 60) // Для bcrypt-хэша
    private String pass;

    @Column(name = "\"balance\"", nullable = false)
    private BigDecimal balance = BigDecimal.valueOf(1000);

    @PrePersist
    protected void onCreate() {
        if (userID == null) {
            userID = UUID.randomUUID();
        }
    }

    public User(String name, String surname, String nickname, String phoneNumber, String email, String pass) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pass = pass;
    }
}
