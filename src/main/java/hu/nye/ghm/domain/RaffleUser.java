package hu.nye.ghm.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@EqualsAndHashCode
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
    @Builder.Default
    private Set<String> roles = Set.of("USER");

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(mappedBy = "players")
    private List<Raffle> raffle = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "winner")
    private List<Raffle> wonRaffles = new ArrayList<>();
}
