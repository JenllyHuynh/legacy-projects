package com.fu.news.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SystemAccount")
@Getter
@Setter
@NoArgsConstructor
public class SystemAccount {

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_STAFF = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private Integer accountId;

    @Column(name = "AccountName", nullable = false, length = 100)
    private String accountName;

    @Column(name = "AccountEmail", nullable = false, unique = true, length = 100)
    private String accountEmail;

    /** Admin = 1, Staff = 2 */
    @Column(name = "AccountRole", nullable = false)
    private Integer accountRole;

    /** BCrypt hash */
    @Column(name = "AccountPassword", nullable = false, length = 100)
    private String accountPassword;
}
