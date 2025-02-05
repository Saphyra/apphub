package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.commodity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommodityEntityId implements Serializable {
    private String externalReference;
    private String commodityName;
}
