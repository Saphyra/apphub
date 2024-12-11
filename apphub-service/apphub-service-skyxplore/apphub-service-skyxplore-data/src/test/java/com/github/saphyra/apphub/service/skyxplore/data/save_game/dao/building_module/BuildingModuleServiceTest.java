package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BuildingModuleServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final Integer PAGE = 32;
    private static final Integer ITEMS_PER_PAGE = 3;

    @Mock
    private BuildingModuleDao buildingModuleDao;

    @InjectMocks
    private BuildingModuleService underTest;

    @Mock
    private BuildingModuleModel model;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.BUILDING_MODULE);
    }

    @Test
    void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        then(buildingModuleDao).should().deleteByGameId(GAME_ID);
    }

    @Test
    void save() {
        underTest.save(List.of(model));

        then(buildingModuleDao).should().saveAll(List.of(model));
    }

    @Test
    void deleteById() {
        underTest.deleteById(BUILDING_MODULE_ID);

        then(buildingModuleDao).should().deleteById(BUILDING_MODULE_ID);
    }

    @Test
    void loadPage() {
        given(buildingModuleDao.getPageByGameId(GAME_ID, PAGE, ITEMS_PER_PAGE)).willReturn(List.of(model));

        assertThat(underTest.loadPage(GAME_ID, PAGE, ITEMS_PER_PAGE)).containsExactly(model);
    }
}