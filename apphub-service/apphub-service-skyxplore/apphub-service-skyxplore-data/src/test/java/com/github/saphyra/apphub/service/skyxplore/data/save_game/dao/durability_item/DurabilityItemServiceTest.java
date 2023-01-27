package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
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
    private DurabilityItemDao durabilityItemDao;

    @Mock
    private DurabilityItemValidator durabilityItemValidator;

    @InjectMocks
    private DurabilityItemService underTest;

    @Mock
    private DurabilityItemModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(durabilityItemDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.DURABILITY_ITEM_MODEL);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(durabilityItemValidator).validate(model);
        verify(durabilityItemDao).saveAll(Arrays.asList(model));
    }

    @Test
    public void findById() {
        given(durabilityItemDao.findById(DURABILITY_ITEM_ID)).willReturn(Optional.of(model));

        Optional<DurabilityItemModel> result = underTest.findById(DURABILITY_ITEM_ID);

        assertThat(result).contains(model);
    }

    @Test
    public void getByParent() {
        given(durabilityItemDao.getByParent(PARENT)).willReturn(Arrays.asList(model));

        List<DurabilityItemModel> result = underTest.getByParent(PARENT);

        assertThat(result).containsExactly(model);
    }

    @Test
    public void deleteById() {
        underTest.deleteById(DURABILITY_ITEM_ID);

        verify(durabilityItemDao).deleteById(DURABILITY_ITEM_ID);
    }
}