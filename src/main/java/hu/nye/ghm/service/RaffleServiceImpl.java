package hu.nye.ghm.service;

import hu.nye.ghm.entity.Raffle;
import hu.nye.ghm.repository.RaffleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RaffleServiceImpl implements RaffleService {
    private final RaffleRepository repository;

    @Override
    public List<RaffleDTO> getAllRaffles() {
        Iterable<Raffle> raffles = repository.findAll();
        List<RaffleDTO> raffleDTOs = new ArrayList<>();

        for (Raffle raffle : raffles) {
            raffleDTOs.add(new RaffleDTO(raffle.getId(), raffle.getName()));
        }

        return raffleDTOs;
    }

    public void createNewRaffle(String raffleName) {
        repository.save(new Raffle(null, raffleName, new ArrayList<>(), null));
    }
}
