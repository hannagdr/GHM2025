package hu.nye.ghm.repository;

import hu.nye.ghm.entity.Player;
import hu.nye.ghm.entity.Prize;
import org.springframework.data.repository.CrudRepository;

public interface PrizeRepository extends CrudRepository<Prize, Long> {
}
