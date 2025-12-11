package hu.nye.ghm.service.impl;

import hu.nye.ghm.domain.Prize;
import hu.nye.ghm.domain.Raffle;
import hu.nye.ghm.repository.PrizeRepository;
import hu.nye.ghm.repository.RaffleRepository;
import hu.nye.ghm.service.RaffleService;
import hu.nye.ghm.web.dto.RaffleDTO;
import hu.nye.ghm.web.dto.response.RaffleIdNameResponse;
import hu.nye.ghm.web.dto.response.RaffleState;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RaffleServiceImpl implements RaffleService {
    private final RaffleRepository raffleRepository;
    private final PrizeRepository prizeRepository;

    @Override
    public List<RaffleIdNameResponse> getAllRaffles() {
        Iterable<Raffle> raffles = raffleRepository.findAll();
        List<RaffleIdNameResponse> raffleIdNameResponses = new ArrayList<>();

        for (Raffle raffle : raffles) {
            RaffleState raffleState = RaffleState.OPEN;
            if (raffle.isClosed() && raffle.getWinner() == null) {
                raffleState = RaffleState.CANCELED;
            } else if (raffle.isClosed()) {
                raffleState = RaffleState.CLOSED;
            }
            raffleIdNameResponses.add(new RaffleIdNameResponse(raffle.getId(), raffle.getName(), raffleState));
        }

        return raffleIdNameResponses;
    }

    @Override
    @Transactional
    public void createNewRaffle(RaffleDTO raffle) {
        Optional<Prize> prize = prizeRepository.findById(raffle.getPrizeId());
        if (prize.isEmpty()) {
            throw new RuntimeException("Cannot find prize with the received id");
        }
        Raffle newRaffle = Raffle.builder().name(raffle.getName()).prize(prize.get()).build();
        raffleRepository.save(newRaffle);
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
