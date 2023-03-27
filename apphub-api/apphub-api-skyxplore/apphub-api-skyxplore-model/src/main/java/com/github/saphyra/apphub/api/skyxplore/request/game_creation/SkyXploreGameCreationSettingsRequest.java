package com.github.saphyra.apphub.api.skyxplore.request.game_creation;

import com.github.saphyra.apphub.lib.common_domain.Range;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class SkyXploreGameCreationSettingsRequest {
    private Integer maxPlayersPerSolarSystem;
    private Range<Integer> additionalSolarSystems;
    private Range<Integer> planetsPerSolarSystem;
    private Range<Integer> planetSize;
}
