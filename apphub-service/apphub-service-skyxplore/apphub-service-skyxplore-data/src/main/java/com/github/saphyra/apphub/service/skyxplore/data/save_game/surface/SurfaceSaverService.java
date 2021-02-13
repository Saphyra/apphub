package com.github.saphyra.apphub.service.skyxplore.data.save_game.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SurfaceSaverService implements GameItemSaver {
    private final SurfaceDao surfaceDao;
    private final SurfaceModelValidator surfaceModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        surfaceDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.SURFACE;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<SurfaceModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof SurfaceModel)
            .map(gameItem -> (SurfaceModel) gameItem)
            .peek(surfaceModelValidator::validate)
            .collect(Collectors.toList());

        surfaceDao.saveAll(models);
    }
}
