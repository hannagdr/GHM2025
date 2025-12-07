package hu.nye.ghm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "RAFFLE")
@NoArgsConstructor
@AllArgsConstructor
public class Raffle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToMany
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
