package hu.nye.ghm.repository;

import hu.nye.ghm.entity.RaffleUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RaffleUserRepository extends CrudRepository<RaffleUser, Long> {
    Optional<RaffleUser> findByUserName(String userName);
}
