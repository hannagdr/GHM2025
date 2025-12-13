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
    Long id;
    String name;
    PrizeDTO prize;
    boolean closed;
    boolean canceled;
    List<String> playerNames;
}
