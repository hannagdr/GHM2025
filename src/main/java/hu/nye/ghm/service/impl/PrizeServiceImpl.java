package hu.nye.ghm.service.impl;

import hu.nye.ghm.repository.PrizeRepository;
import hu.nye.ghm.service.PrizeService;
import hu.nye.ghm.web.dto.PrizeComboBoxDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PrizeServiceImpl implements PrizeService {
    private final PrizeRepository prizeRepository;

    @Override
    public List<PrizeComboBoxDTO> getPrizesForComboBox() {
        return StreamSupport.stream(prizeRepository.findAll().spliterator(), false)
                .map(prize -> new PrizeComboBoxDTO(prize.getId(), prize.getName()))
                .toList();
    }
}
