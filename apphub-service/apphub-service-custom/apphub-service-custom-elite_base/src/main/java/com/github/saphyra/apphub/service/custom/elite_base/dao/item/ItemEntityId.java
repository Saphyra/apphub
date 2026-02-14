package com.github.saphyra.apphub.service.custom.elite_base.dao.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ItemEntityId implements Serializable {
    private String externalReference;
    private String itemName;
}
