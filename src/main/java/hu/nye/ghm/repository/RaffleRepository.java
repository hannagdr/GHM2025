package hu.nye.ghm.repository;

import hu.nye.ghm.entity.Raffle;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RaffleRepository extends CrudRepository<Raffle, Long> {
}
