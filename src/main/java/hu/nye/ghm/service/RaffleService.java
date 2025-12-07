package hu.nye.ghm.service;

import hu.nye.ghm.entity.Raffle;
import hu.nye.ghm.repository.RaffleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RaffleService {
    private final RaffleRepository repository;

    public String createNewRaffle(String raffleName) {
        repository.save(new Raffle(null, raffleName, new ArrayList<>(), null));
        return raffleName;
    }
}
