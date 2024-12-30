package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.fc_materials_journal.FcMaterialsJournalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FcMaterialsJournalMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FC_MATERIALS_JOURNAL.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FcMaterialsJournalMessage fcMaterialsJournalMessage = objectMapperWrapper.readValue(message.getMessage(), FcMaterialsJournalMessage.class);

        //TODO implement
    }
}
