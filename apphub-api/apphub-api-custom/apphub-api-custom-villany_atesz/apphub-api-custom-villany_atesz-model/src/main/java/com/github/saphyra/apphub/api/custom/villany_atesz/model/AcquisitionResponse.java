package com.github.saphyra.apphub.api.custom.villany_atesz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AcquisitionResponse {
    private UUID acquisitionId;
    private UUID stockItemId;
    private String stockItemName;
    private Integer amount;
}
