package com.github.saphyra.apphub.service.elite_base.message_processing.structure.fc_materials_capi;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FcMaterialCapiItem {
    private Long id;
    private String name;
    private Integer outstanding;
    private Integer stock;
    private Integer price;
    private Integer total;
}
