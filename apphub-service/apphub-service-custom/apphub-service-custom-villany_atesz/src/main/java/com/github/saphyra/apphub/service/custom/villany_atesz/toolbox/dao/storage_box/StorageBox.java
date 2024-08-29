package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class StorageBox {
    private final UUID storageBoxId;
    private final UUID userId;
    private String name;
}
