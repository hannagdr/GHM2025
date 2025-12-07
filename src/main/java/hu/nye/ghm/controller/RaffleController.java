package hu.nye.ghm.controller;

import hu.nye.ghm.controller.requests.CreateRaffleRequest;
import hu.nye.ghm.service.RaffleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RaffleController {
    private final RaffleServiceImpl raffleService;

    @PostMapping("/raffle")
    public String createRaffle(@ModelAttribute("newRaffle") CreateRaffleRequest newRaffle) {
        raffleService.createNewRaffle(newRaffle.getRaffleName());
        return "redirect:/";
    }
}
