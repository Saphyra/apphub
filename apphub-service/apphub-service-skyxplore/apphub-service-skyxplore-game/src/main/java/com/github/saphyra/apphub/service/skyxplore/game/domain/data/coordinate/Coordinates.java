package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <ReferenceId, Coordinate>
 */
public class Coordinates extends ConcurrentHashMap<UUID, CoordinateModel> {
}
