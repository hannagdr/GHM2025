package hu.nye.ghm.service;

import java.util.List;

public interface RaffleService {
    List<RaffleDTO> getAllRaffles();

    void createNewRaffle(String raffleName);
}
