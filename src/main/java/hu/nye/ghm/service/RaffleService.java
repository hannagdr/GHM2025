package hu.nye.ghm.service;

import hu.nye.ghm.web.dto.RaffleDTO;
import hu.nye.ghm.web.dto.RaffleListTableDTO;

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
     * Meglévő nyeremény hozzárendelése a tombolához
     *
     * @param raffleId Tombola azonosító
     * @param prizeId  Nyeremény azonosító
     */
    void addPrizeToRaffle(Long raffleId, Long prizeId);

    /**
     * Új nyeremény hozzárendelése a tombolához. A folyamat során a nyeremény nevével létre lesz hozva egy új
     * nyeremény, majd az hozzárendelve a tombolához.
     *
     * @param raffleId  Tombola azonosító
     * @param prizeName Nyeremény azonosító
     */
    void addNewPrizeToRaffle(Long raffleId, String prizeName);

    /**
     * A kapott ID alapján visszaadja a Raffle-ből késztített DTO-t ha van ilyen.
     *
     * @param id - Raffle ID
     * @return A létező RaffleDTO
     */
    RaffleDTO getRaffleById(Long id);
}
