package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DurabilityItemServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID DURABILITY_ITEM_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private DurabilityDao durabilityDao;

    @InjectMocks
    private DurabilityItemService underTest;

    @Mock
    private DurabilityModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(durabilityDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.DURABILITY);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(durabilityDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(durabilityDao.findById(DURABILITY_ITEM_ID)).willReturn(Optional.of(model));

        Optional<DurabilityModel> result = underTest.findById(DURABILITY_ITEM_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(DURABILITY_ITEM_ID);

        verify(durabilityDao).deleteById(DURABILITY_ITEM_ID);
    }
}