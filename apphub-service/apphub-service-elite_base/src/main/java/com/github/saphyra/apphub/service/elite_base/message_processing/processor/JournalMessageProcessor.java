package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.CarrierJumpJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.DockedJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.FsdJumpJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.LocationJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.SaaSignalFoundJournalMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message.ScanJournalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class JournalMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.JOURNAL.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        String event = objectMapperWrapper.readTree(message.getMessage())
            .get("event")
            .asText();

        //TODO implement processing

        switch (event) {
            case "Scan":
                ScanJournalMessage scanJournalMessage = objectMapperWrapper.readValue(message.getMessage(), ScanJournalMessage.class);
                break;
            case "FSDJump":
                FsdJumpJournalMessage fsdJumpJournalMessage = objectMapperWrapper.readValue(message.getMessage(), FsdJumpJournalMessage.class);
                break;
            case "Docked":
                DockedJournalMessage dockedJournalMessage = objectMapperWrapper.readValue(message.getMessage(), DockedJournalMessage.class);
                break;
            case "CarrierJump":
                CarrierJumpJournalMessage carrierJumpJournalMessage = objectMapperWrapper.readValue(message.getMessage(), CarrierJumpJournalMessage.class);
                break;
            case "Location":
                LocationJournalMessage locationJournalMessage = objectMapperWrapper.readValue(message.getMessage(), LocationJournalMessage.class);
                break;
            case "SAASignalsFound":
                SaaSignalFoundJournalMessage saaSignalFoundJournalMessage = objectMapperWrapper.readValue(message.getMessage(), SaaSignalFoundJournalMessage.class);
                break;
            default:
                throw new RuntimeException("Unhandled event: " + event);
        }
    }
}
