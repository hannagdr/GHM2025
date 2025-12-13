package hu.nye.ghm.service.impl;

import hu.nye.ghm.domain.Prize;
import hu.nye.ghm.domain.Raffle;
import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.PrizeRepository;
import hu.nye.ghm.repository.RaffleRepository;
import hu.nye.ghm.service.RaffleService;
import hu.nye.ghm.web.dto.PrizeDTO;
import hu.nye.ghm.web.dto.RaffleDTO;
import hu.nye.ghm.web.dto.RaffleListTableDTO;
import hu.nye.ghm.web.dto.RaffleViewDTO;
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
    public List<RaffleListTableDTO> getAllRaffles() {
        Iterable<Raffle> raffles = raffleRepository.findAll();
        List<RaffleListTableDTO> raffleIdNameResponse = new ArrayList<>();

        for (Raffle raffle : raffles) {
            boolean isCanceled = raffle.isClosed() && raffle.getWinner() == null;
            raffleIdNameResponse.add(new RaffleListTableDTO(raffle.getId(), raffle.getName(), raffle.isClosed(), isCanceled));
        }

        return raffleIdNameResponse;
    }

    @Override
    @Transactional
    public void createOrUpdateRaffle(RaffleDTO raffleDTO) {
        Optional<Prize> prize = prizeRepository.findById(raffleDTO.getPrizeId());
        if (prize.isEmpty()) {
            throw new RuntimeException("Cannot find prize with the received id");
        }
        Raffle raffle;
        if (raffleDTO.getId() == null) {
            raffle = Raffle.builder().name(raffleDTO.getName()).prize(prize.get()).build();
        } else {
            raffle = raffleRepository.findById(raffleDTO.getId())
                    .map(rf -> {
                        rf.setName(raffleDTO.getName());
                        rf.setPrize(prize.get());
                        return rf;
                    })
                    .orElseThrow(() -> new RuntimeException("Raffle cannot be found by id"));
        }
        raffleRepository.save(raffle);
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

    @Override
    public RaffleViewDTO getRaffleViewData(Long id) {
        Optional<Raffle> raffleOpt = raffleRepository.findById(id);
        if (raffleOpt.isEmpty()) {
            throw new RuntimeException("No raffle found with id");
        }
        Raffle raffle = raffleOpt.get();
        Prize prize = raffle.getPrize();

        PrizeDTO prizeDTO = prize == null ? null : PrizeDTO.builder()
                .category(prize.getCategory())
                .name(prize.getName())
                .build();

        List<String> playerNames = raffle.getPlayers().stream().map(RaffleUser::getName).toList();

        return RaffleViewDTO.builder()
                .name(raffle.getName())
                .id(raffle.getId())
                .closed(raffle.isClosed())
                .canceled(raffle.isClosed() && raffle.getWinner() == null)
                .prize(prizeDTO)
                .playerNames(playerNames)
                .build();
    }
}
