package com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.Signal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SaaSignalFoundJournalMessage extends JournalMessage {
    @JsonProperty("Signals")
    private Signal[] signals;

    @JsonProperty("Genuses")
    private Map<String, String>[] genuses;
}
