package hu.nye.ghm.utility;

import hu.nye.ghm.entity.Raffle;
import hu.nye.ghm.repository.PlayerRepository;
import hu.nye.ghm.repository.PrizeRepository;
import hu.nye.ghm.repository.RaffleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaffleTestDataSupplierUtility {
    private final RaffleRepository raffleRepository;
    private final PrizeRepository prizeRepository;
    private final PlayerRepository playerRepository;

    @PostConstruct
    public void setup() {
        deleteAllData();
        Raffle raffleSmartWatch = new Raffle(null, "Okosóra", List.of(), null);
        Raffle raffleConsole = new Raffle(null, "Játékkonzol", List.of(), null);
        Raffle raffleCellphone = new Raffle(null, "Mobiltelefon", List.of(), null);


    }

    private void deleteAllData() {
        this.raffleRepository.deleteAll();
        this.prizeRepository.deleteAll();
        this.playerRepository.deleteAll();
    }
}
