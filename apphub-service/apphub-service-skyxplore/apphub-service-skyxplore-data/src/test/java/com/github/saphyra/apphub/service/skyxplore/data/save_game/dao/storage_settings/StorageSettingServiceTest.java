package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StorageSettingServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID ID = UUID.randomUUID();
    private static final Integer PAGE = 245;
    private static final Integer ITEMS_PER_PAGE = 346;

    @Mock
    private StorageSettingDao dao;

    @InjectMocks
    private StorageSettingService underTest;

    @Mock
    private StorageSettingModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(dao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.STORAGE_SETTING);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(dao).saveAll(Arrays.asList(model));
    }

    @Test
    public void deleteById() {
        underTest.deleteById(ID);

        verify(dao).deleteById(ID);
    }

    @Test
    void loadPage() {
        given(dao.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).willReturn(List.of(model));

        assertThat(underTest.loadPage(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}