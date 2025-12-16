package hu.nye.ghm.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleViewDTO {
    private Long id;
    private String name;
    private PrizeDTO prize;
    private boolean closed;
    private boolean canceled;
    private boolean alreadyApplied;
    private List<String> playerNames;
    private String winnerUsername;
}
