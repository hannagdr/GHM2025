package hu.nye.ghm.service.impl;

import hu.nye.ghm.domain.Raffle;
import hu.nye.ghm.repository.RaffleRepository;
import hu.nye.ghm.service.RaffleService;
import hu.nye.ghm.web.dto.response.RaffleIdNameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RaffleServiceImpl implements RaffleService {
    private final RaffleRepository repository;

    @Override
    public List<RaffleIdNameResponse> getAllRaffles() {
        Iterable<Raffle> raffles = repository.findAll();
        List<RaffleIdNameResponse> raffleIdNameResponses = new ArrayList<>();

        for (Raffle raffle : raffles) {
            raffleIdNameResponses.add(new RaffleIdNameResponse(raffle.getId(), raffle.getName()));
        }

        return raffleIdNameResponses;
    }

    @Override
    public void createNewRaffle(String raffleName) {
        repository.save(new Raffle(null, raffleName, new ArrayList<>(), null));
    }

    @Override
    public void drawRaffle(Long raffleId) {
        throw new RuntimeException("drawRaffle is not implemented");
    }

    @Override
    public void closeRaffle(Long raffleId) {
        throw new RuntimeException("closeRaffle is not implemented");
    }

    @Override
    public void addPrizeToRaffle(Long raffleId, Long prizeId) {
        throw new RuntimeException("addPrizeToRaffle is not implemented");
    }

    @Override
    public void addNewPrizeToRaffle(Long raffleId, String prizeName) {
        throw new RuntimeException("addNewPrizeToRaffle is not implemented");
    }
}
