package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SurfaceLoader extends AutoLoader<SurfaceModel, Surface> {
    public SurfaceLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.SURFACE;
    }

    @Override
    protected Class<SurfaceModel[]> getArrayClass() {
        return SurfaceModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<Surface> items) {
        gameData.getSurfaces()
            .addAll(items);
    }

    @Override
    protected Surface convert(SurfaceModel model) {
        return Surface.builder()
            .surfaceId(model.getId())
            .planetId(model.getPlanetId())
            .surfaceType(SurfaceType.valueOf(model.getSurfaceType()))
            .build();
    }
}
