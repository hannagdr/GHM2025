package hu.nye.ghm.repository;

import hu.nye.ghm.domain.RaffleUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RaffleUserRepository extends CrudRepository<RaffleUser, Long> {
    /**
     * Felhasználó lekérdezése a felhasználónév alapján.
     *
     * @param userName Felhasználónév
     * @return
     */
    Optional<RaffleUser> findByUserName(String userName);

    /**
     * Validációs lekérdezés, hogy létezik-e felhasználó az adott paraméterek valamelyikével.
     * @param userName Felhasználónév
     * @param emailAddress E-mail cím
     * @return
     */
    boolean existsRaffleUserByUserNameOrEmailAddress(String userName, String emailAddress);
}
