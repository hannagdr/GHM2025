package hu.nye.ghm.service.impl;

import hu.nye.ghm.domain.Prize;
import hu.nye.ghm.repository.PrizeRepository;
import hu.nye.ghm.web.dto.PrizeComboBoxDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PrizeServiceImplTest {

    private PrizeRepository prizeRepository;
    private PrizeServiceImpl prizeService;

    @BeforeEach
    void setUp() {
        this.prizeRepository = mock(PrizeRepository.class);
        this.prizeService = spy(new PrizeServiceImpl(this.prizeRepository));
    }

    @Test
    @DisplayName("Get prizes for the combobox")
    void getPrizesForComboBox() {
        Prize prizeOne = new Prize(10L, "PrizeOne", null, List.of());
        Prize prizeTwo = new Prize(11L, "PrizeTwo", null, List.of());
        when(prizeRepository.findAll()).thenReturn(List.of(prizeOne, prizeTwo));

        List<PrizeComboBoxDTO> prizesForComboBox = this.prizeService.getPrizesForComboBox();
        assertEquals(2, prizesForComboBox.size());
        assertEquals(prizeOne.getName(), prizesForComboBox.get(0).getName());
        assertEquals(prizeOne.getId(), prizesForComboBox.get(0).getId());
        assertEquals(prizeTwo.getName(), prizesForComboBox.get(1).getName());
        assertEquals(prizeTwo.getId(), prizesForComboBox.get(1).getId());
    }
}