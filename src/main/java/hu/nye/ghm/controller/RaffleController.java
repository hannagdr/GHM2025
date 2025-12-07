package hu.nye.ghm.controller;

import hu.nye.ghm.model.CreateRaffle;
import hu.nye.ghm.service.RaffleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("raffle")
@RequiredArgsConstructor
public class RaffleController {
    private RaffleService raffleService;

    @PostMapping
    public String createRaffle(@ModelAttribute("newRaffle") CreateRaffle newRaffle) {
        raffleService.createNewRaffle(newRaffle.getRaffleName());
        return "ok";
    }
}
