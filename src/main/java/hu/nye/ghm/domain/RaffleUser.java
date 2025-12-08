package hu.nye.ghm.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "RAFFLE_PLAYER")
public class RaffleUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String emailAddress;

    @Column
    private String name;

    @Column(nullable = false)
    private String password;

    @Column
    private Set<String> roles;

    @ToString.Exclude
    @ManyToMany(mappedBy = "players")
    private List<Raffle> raffle = new ArrayList<>();
}
