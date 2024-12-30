package com.github.saphyra.apphub.service.elite_base.message_processing.structure.fc_materials_capi;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FcMaterialCapiItems {
    private FcMaterialCapiItem[] purchases;
    private Object sales; //Array if empty, Map<Long, FcMaterialCapiItem> if filled
}
