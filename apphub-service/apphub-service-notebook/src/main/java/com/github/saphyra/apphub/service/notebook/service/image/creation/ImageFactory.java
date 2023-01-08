package com.github.saphyra.apphub.service.notebook.service.image.creation;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.image.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ImageFactory {
    private final IdGenerator idGenerator;

    public Image create(UUID userId, UUID parent, UUID fileId) {
        return Image.builder()
            .imageId(idGenerator.randomUuid())
            .userId(userId)
            .parent(parent)
            .fileId(fileId)
            .build();
    }
}
