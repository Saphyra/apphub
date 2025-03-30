package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CodexEntryJournalMessage extends JournalMessage {
    //Unused
    @JsonProperty("Category")
    private String category;

    @JsonProperty("EntryID")
    private Long entryId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Region")
    private String region;

    @JsonProperty("SubCategory")
    private String subCategory;

    @JsonProperty("SystemAddress")
    private Long systemAddress;
}
