package org.example.cookercorner.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.cookercorner.enums.TokenType;


@Entity
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;

    private boolean revoked;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;




}
