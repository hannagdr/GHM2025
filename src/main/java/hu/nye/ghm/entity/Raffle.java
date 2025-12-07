package hu.nye.ghm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "RAFFLE")
@NoArgsConstructor
@AllArgsConstructor
public class Raffle {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String name;


    @OneToMany(targetEntity = Player.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Player> players;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prize_id")
    private Prize prize;
}
