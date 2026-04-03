package com.github.saphyra.apphub.service.feature.skyxplore.game.admin.service;

import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataDetails;
import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataEntry;
import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataReference;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.feature.skyxplore.game.domain.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class GameAdminQueryService implements AdminQueryService {
    private static final List<GameItemType> EXCLUDED_TYPES = List.of(
        GameItemType.GAME
    );

    private final GameDao gameDao;
    private final ObjectMapper objectMapper;

    @Override
    public GameItemType getType() {
        return GameItemType.GAME;
    }

    @Override
    public List<SkyXploreGameDataEntry> getAll(@Nullable UUID gameId) {
        return gameDao.getAll()
            .stream()
            .map(game -> SkyXploreGameDataEntry.builder()
                .id(game.getGameId())
                .data(toView(game))
                .build())
            .toList();
    }

    @Override
    public SkyXploreGameDataDetails findById(UUID gameId, UUID itemId) {
        return gameDao.findById(gameId)
            .map(game -> SkyXploreGameDataDetails.builder()
                .id(gameId)
                .data(toView(game))
                .refersTo(getReferences())
                .referencedBy(List.of())
                .build())
            .orElseThrow(() -> ExceptionFactory.notFound("Game not found by id " + gameId));
    }

    private List<SkyXploreGameDataReference> getReferences() {
        return Arrays.stream(GameItemType.values())
            .filter(gameItemType -> !EXCLUDED_TYPES.contains(gameItemType))
            .map(gameItemType -> SkyXploreGameDataReference.builder()
                .type(gameItemType)
                .build())
            .toList();
    }

    private GameView toView(Game game) {
        return objectMapper.convertValue(game, GameView.class);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    static class GameView {
        private UUID gameId;
        private String gameName;
        private UUID host;
        private LocalDateTime lastPlayed;
        private Boolean markedForDeletion;
        private LocalDateTime markedForDeletionAt;
        private LocalDateTime expiresAt;
        private Boolean gamePaused;
        private Boolean terminated;
        private Long tick;
    }
}
