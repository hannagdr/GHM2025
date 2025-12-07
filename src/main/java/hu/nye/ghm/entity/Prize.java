package hu.nye.ghm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "PRIZE")
public class Prize {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String category;

    @OneToMany(targetEntity = Player.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Raffle> raffles;
}
