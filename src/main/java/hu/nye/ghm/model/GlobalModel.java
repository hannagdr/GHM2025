package hu.nye.ghm.model;

import hu.nye.ghm.service.RaffleService;
import hu.nye.ghm.web.dto.response.RaffleIdNameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModel {
    private final RaffleService raffleService;

    @ModelAttribute("raffles")
    public List<RaffleIdNameResponse> raffles() {
        return raffleService.getAllRaffles();
    }
}
