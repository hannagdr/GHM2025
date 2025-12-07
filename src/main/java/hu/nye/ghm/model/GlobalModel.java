package hu.nye.ghm.model;

import hu.nye.ghm.service.RaffleDTO;
import hu.nye.ghm.service.RaffleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModel {
    private final RaffleService raffleService;

    @ModelAttribute("raffles")
    public List<RaffleDTO> raffles() {
        return raffleService.getAllRaffles();
    }
}
