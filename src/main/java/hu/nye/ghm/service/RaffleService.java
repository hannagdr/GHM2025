package hu.nye.ghm.service;

import hu.nye.ghm.web.dto.response.RaffleIdNameResponse;

import java.util.List;

public interface RaffleService {
    List<RaffleIdNameResponse> getAllRaffles();

    void createNewRaffle(String raffleName);
}
