package hu.nye.ghm.repository;

import hu.nye.ghm.entity.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {
}
