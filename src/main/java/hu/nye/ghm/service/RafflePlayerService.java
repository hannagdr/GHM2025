package hu.nye.ghm.service;

import hu.nye.ghm.domain.RaffleUser;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * A felhasználókkal kapcsolatos módosítások kezelését szolgáló Service.
 */
public interface RafflePlayerService extends UserDetailsService {
    /**
     * Új felhasználó regisztrálása
     *
     * @param user
     * @return false - Ha az felhasználónév vagy az e-mail cím már foglalt
     * @return true - Ha a regisztráció sikeres
     */
    boolean registerNewUser(RaffleUser user);
}
