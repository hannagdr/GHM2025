package hu.nye.ghm.repository;

import hu.nye.ghm.domain.RaffleUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RaffleUserRepository extends CrudRepository<RaffleUser, Long> {
    Optional<RaffleUser> findByUserName(String userName);
    Optional<RaffleUser> findByEmailAddress(String emailAddress);

    boolean existsRaffleUserByUserNameOrEmailAddress(String userName, String emailAddress);
}
