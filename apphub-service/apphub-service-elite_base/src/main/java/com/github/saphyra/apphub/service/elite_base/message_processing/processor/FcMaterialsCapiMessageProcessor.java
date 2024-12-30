package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.fc_materials_capi.FcMaterialsCapiMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FcMaterialsCapiMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FC_MATERIALS_CAPI.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FcMaterialsCapiMessage fcMaterialsCapiMessage = objectMapperWrapper.readValue(message.getMessage(), FcMaterialsCapiMessage.class);

        //TODO implement
    }
}
