package com.github.saphyra.apphub.service.skyxplore.game.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OpenedPage {
    public static final OpenedPage DEFAULT_PAGE = builder().pageType(OpenedPageType.NONE).build();

    private OpenedPageType pageType;
    private UUID pageId;
}
