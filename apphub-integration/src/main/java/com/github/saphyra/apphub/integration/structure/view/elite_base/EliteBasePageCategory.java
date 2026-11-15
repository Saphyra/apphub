package com.github.saphyra.apphub.integration.structure.view.elite_base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EliteBasePageCategory {
    SEARCH_NEAREST("elite-base-navigation-button-search_nearest"),
    TRADE("elite-base-navigation-button-commodity_trading"),
    FARM_MERITS("elite-base-navigation-button-merit_farming"),
    FOR_NAKATO_KAINE("elite-base-navigation-button-merit_farming_nakato_kaine");

    private final String categoryId;
}
