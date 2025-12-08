package hu.nye.ghm.controller;

import hu.nye.ghm.service.RaffleService;
import hu.nye.ghm.web.dto.request.CreateRaffleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * A tombolához kapcsolódó Controller osztály
 */
@Controller
@RequiredArgsConstructor
public class RaffleController {
    private final RaffleService raffleService;

    /**
     * A kapott paraméter alapján létrehozza a Tombolát, és visszatér a főoldalra.
     *
     * @param newRaffle Az új Tombolának a neve
     */
    @PostMapping("/raffle")
    public String createRaffle(@ModelAttribute("newRaffle") CreateRaffleRequest newRaffle) {
        raffleService.createNewRaffle(newRaffle.raffleName());
        return "redirect:/";
    }
}
