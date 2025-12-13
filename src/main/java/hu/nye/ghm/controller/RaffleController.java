package hu.nye.ghm.controller;

import hu.nye.ghm.service.PrizeService;
import hu.nye.ghm.service.RaffleService;
import hu.nye.ghm.web.dto.RaffleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String newRaffle(Model model) {
        model.addAttribute("raffle", new RaffleDTO());
        model.addAttribute("prizes", prizeService.getPrizesForComboBox());
        return "raffle";
    }

    @GetMapping("/raffle/{id}/edit")
    public String editRaffle(@PathVariable("id") Long raffleId, Model model) {
        model.addAttribute("raffle", raffleService.getRaffleById(raffleId));
        model.addAttribute("prizes", prizeService.getPrizesForComboBox());
        return "raffle";
    }

    @GetMapping("/raffle/{id}")
    public String viewRaffle(@PathVariable("id") Long raffleId, Model model) {
        model.addAttribute("raffle", raffleService.getRaffleById(raffleId));
        return "raffle_view";
    }

    /**
     * A kapott paraméter alapján létrehozza a Tombolát, és visszatér a főoldalra.
     *
     * @param raffle Az új Tombolának a neve
     */
    @PostMapping("/raffle")
    public String saveRaffle(@ModelAttribute RaffleDTO raffle) {
        raffleService.createOrUpdateRaffle(raffle);
        return "redirect:/";
    }
}
