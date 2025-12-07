package hu.nye.ghm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "PLAYER")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String playerName;

    @Column
    private String emailAddress;

    @ToString.Exclude
    @ManyToMany(mappedBy = "players")
    private List<Raffle> raffle = new ArrayList<>();
}
