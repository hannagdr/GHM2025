package hu.nye.ghm.controller;

import hu.nye.ghm.service.PrizeService;
import hu.nye.ghm.service.RaffleService;
import hu.nye.ghm.web.dto.RaffleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * A tombolához kapcsolódó Controller osztály
 */
@Controller
@RequiredArgsConstructor
public class RaffleController {
    private final RaffleService raffleService;
    private final PrizeService prizeService;

    @GetMapping("/raffle")
    public String openNewRafflePage(Model model) {
        model.addAttribute("raffle", new RaffleDTO());
        model.addAttribute("prizes", prizeService.getPrizesForComboBox());
        return "raffle";
    }

    /**
     * A kapott paraméter alapján létrehozza a Tombolát, és visszatér a főoldalra.
     *
     * @param raffle Az új Tombolának a neve
     */
    @PostMapping("/raffle")
    public String saveRaffle(@ModelAttribute RaffleDTO raffle) {
        raffleService.createNewRaffle(raffle);
        return "redirect:/";
    }
}
