package hu.nye.ghm.service;

import hu.nye.ghm.web.dto.RaffleDTO;
import hu.nye.ghm.web.dto.RaffleListTableDTO;
import hu.nye.ghm.web.dto.RaffleViewDTO;

import java.util.List;

/**
 * A tombolához szükséges alap függvények gyűjteménye.
 */
public interface RaffleService {
    /**
     * Az összes tombola lekérdezése
     *
     * @return Az összes tombola, ami az adatbázisban található.
     */
    List<RaffleListTableDTO> getAllRaffles();

    /**
     * Új tombola létrehozása vagy frissítése. Az újonnan létrehozott tombolához nincs felhasználó tárolva, illetve jutalom rendelve.
     *
     * @param raffle A tombola neve és hozzárendelt nyeremény.
     */
    void createOrUpdateRaffle(RaffleDTO raffle);

    /**
     * Sorsolás indítása a tombolához
     *
     * @param raffleId Tombola azonosító
     */
    void drawRaffle(Long raffleId);

    /**
     * Tombola lezárása
     *
     * @param raffleId Tombola azonosító
     */
    void closeRaffle(Long raffleId);

    /**
     * A kapott ID alapján visszaadja a Raffle-ből késztített DTO-t ha van ilyen.
     *
     * @param id - Raffle ID
     * @return A létező RaffleDTO
     */
    RaffleViewDTO getRaffleViewData(Long id);

    /**
     * A kapott ID alapján visszaadja a Raffle-ből késztített DTO-t ha van ilyen.
     *
     * @param id - Raffle ID
     * @return A létező RaffleDTO
     */
    RaffleDTO getRaffleEditData(Long id);

    /**
     * Megkeresi a kapott ID és felhasználónév alapján a Raffle-t és RaffleUser-t. Amennyiben mind a kettő létezik,
     * hozzáadja a User-t a játékosokhoz.
     *
     * @param raffleId A Raffle azonosítója
     * @param userName A felhasználó neve
     */
    void applyToRaffle(Long raffleId, String userName);
}
