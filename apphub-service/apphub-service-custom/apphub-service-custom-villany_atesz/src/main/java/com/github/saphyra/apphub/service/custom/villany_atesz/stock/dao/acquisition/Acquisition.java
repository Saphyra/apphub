package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Acquisition {
    private final UUID acquisitionId;
    private final UUID userId;
    private final LocalDate acquiredAt;
    private final UUID stockItemId;
    private final int amount;
}
