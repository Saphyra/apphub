package com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConvoyModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.CoordinateConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ConvoyConverter {
    private final CoordinateConverter coordinateConverter;

    public ConvoyModel toModel(UUID gameId, Convoy convoy) {
        ConvoyModel model = new ConvoyModel();
        model.setId(convoy.getConvoyId());
        model.setGameId(gameId);
        model.setType(GameItemType.CONVOY);
        model.setResourceDeliveryRequestId(convoy.getResourceDeliveryRequestId());
        model.setCapacity(convoy.getCapacity());
        model.setRoute(convoy.getRoute().stream().map(coordinate -> coordinateConverter.convert(gameId, coordinate)).toList());

        return model;
    }
}
