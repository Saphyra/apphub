package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.message;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public abstract class JournalMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("SystemAddress")
    private Long starId;

    @JsonProperty("StarSystem")
    @JsonAlias("System")
    private String starName;

    @JsonProperty("StarPos")
    private Double[] starPosition;

    @JsonProperty("BodyID")
    private Long bodyId;

    @JsonProperty("BodyName")
    @JsonAlias("Body")
    private String bodyName;

    //Unused
    private Boolean horizons;
    private Boolean odyssey;
}
