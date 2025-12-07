package hu.nye.ghm.utility;

import hu.nye.ghm.entity.Player;
import hu.nye.ghm.entity.Prize;
import hu.nye.ghm.entity.Raffle;
import hu.nye.ghm.repository.PlayerRepository;
import hu.nye.ghm.repository.PrizeRepository;
import hu.nye.ghm.repository.RaffleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        Raffle raffleSmartWatch = raffleRepository.save(new Raffle(null, "Most jött el a Te időd: Csuklódra szabott szerencse!", new ArrayList<>(), null));
        Raffle raffleConsole = raffleRepository.save(new Raffle(null, "Lépj szintet a szórakozásban – Tiéd lehet a csúcskonzol!", new ArrayList<>(),  null));
        Raffle raffleCellphone = raffleRepository.save(new Raffle(null, "Hív a szerencse: Zsebre vágható csúcstechnika!", new ArrayList<>() , null));

        Player playerOne = playerRepository.save(new Player(null, "Test Elek", "test.elek@mail.eu", new ArrayList<>()));
        Player playerTwo = playerRepository.save(new Player(null, "Test Etel", "test.etel@mail.eu", new ArrayList<>()));
        Player playerThree = playerRepository.save(new Player(null, "Test Eugén", "test.eugen@mail.eu", new ArrayList<>()));

        Prize ps5Pro = prizeRepository.save(new Prize(null, "Playstation 5 Pro", "Játékkonzol", new ArrayList<>()));
        Prize appleIphone17 = prizeRepository.save(new Prize(null, "Apple iPhone 17 Pro Max 2TB", "Mobiltelefon", new ArrayList<>()));
        Prize garminSmartWatch = prizeRepository.save(new Prize(null, "Garmin fēnix® 51mm AMOLED", "Okosóra", new ArrayList<>()));

        raffleSmartWatch.getPlayers().add(playerOne);
        raffleSmartWatch.setPrize(garminSmartWatch);

        raffleCellphone.getPlayers().addAll(List.of(playerOne, playerTwo));
        raffleCellphone.setPrize(appleIphone17);

        raffleConsole.getPlayers().addAll(List.of(playerOne, playerTwo, playerThree));
        raffleConsole.setPrize(ps5Pro);
        raffleRepository.saveAll(List.of(raffleCellphone, raffleConsole, raffleSmartWatch));
    }

    private void deleteAllData() {
        this.raffleRepository.deleteAll();
        this.prizeRepository.deleteAll();
        this.playerRepository.deleteAll();
    }
}
