package hu.nye.ghm.repository;

import hu.nye.ghm.domain.Raffle;
import org.springframework.data.repository.CrudRepository;

public interface RaffleRepository extends CrudRepository<Raffle, Long> {
}
