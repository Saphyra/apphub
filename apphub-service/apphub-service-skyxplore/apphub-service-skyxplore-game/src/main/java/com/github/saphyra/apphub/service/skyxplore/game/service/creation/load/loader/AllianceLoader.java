package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
class AllianceLoader {
    private final GameItemLoader gameItemLoader;

    Map<UUID, Alliance> load(UUID gameId, Map<UUID, Player> players) {
        List<AllianceModel> alliances = gameItemLoader.loadChildren(gameId, GameItemType.ALLIANCE, AllianceModel[].class);
        return alliances.stream()
            .map(allianceModel -> convert(allianceModel, players))
            .collect(Collectors.toMap(Alliance::getAllianceId, Function.identity()));
    }

    private Alliance convert(AllianceModel model, Map<UUID, Player> players) {
        return Alliance.builder()
            .allianceId(model.getId())
            .allianceName(model.getName())
            .members(getMembers(model.getId(), players))
            .build();
    }

    private Map<UUID, Player> getMembers(UUID allianceId, Map<UUID, Player> players) {
        return players.values()
            .stream()
            .filter(player -> allianceId.equals(player.getAllianceId()))
            .collect(Collectors.toMap(Player::getUserId, Function.identity()));
    }
}
