package hu.nye.ghm.service;

import hu.nye.ghm.web.dto.PrizeComboBoxDTO;

import java.util.List;

/**
 * A nyereménnyel kapcsolatos módosítások kezelését szolgáló Service.
 */
public interface PrizeService {
    /**
     * Lekérdezni az adatbázisból a id-name párosokat az összes Prize-hoz.
     *
     * @return PrizeComboBoxDTO lista
     */
    List<PrizeComboBoxDTO> getPrizesForComboBox();
}
