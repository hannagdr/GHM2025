package hu.nye.ghm.model;

import hu.nye.ghm.service.RaffleService;
import hu.nye.ghm.web.dto.RaffleListTableDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * A globálisan elérhető változókat tartalmazó osztály
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModel {
    private final RaffleService raffleService;

    /**
     * Hozzáadja a "raffles" globálisan elérhető változóhoz a tombolák listáját
     *
     * @return A tombolák azonosító - név párosa
     */
    @ModelAttribute("raffles")
    public List<RaffleListTableDTO> raffles() {
        return raffleService.getAllRaffles();
    }
}
