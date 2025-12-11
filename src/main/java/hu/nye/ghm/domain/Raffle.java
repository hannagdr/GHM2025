package hu.nye.ghm.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity(name = "RAFFLE")
@NoArgsConstructor
@AllArgsConstructor
public class Raffle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    @Builder.Default
    private boolean closed = false;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "winner_id")
    private RaffleUser winner;

    @ManyToMany
    @Builder.Default
    @ToString.Exclude
    @JoinTable(
            name="raffle_players",
            joinColumns = @JoinColumn(name = "raffle_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id"))
    private List<RaffleUser> players = new ArrayList<>();

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "prize_id")
    private Prize prize;
}
