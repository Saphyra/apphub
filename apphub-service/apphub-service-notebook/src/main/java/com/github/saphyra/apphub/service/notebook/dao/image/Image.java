package com.github.saphyra.apphub.service.notebook.dao.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Image {
    private final UUID imageId;
    private final UUID userId;
    private final UUID parent;
    private UUID fileId;
}
