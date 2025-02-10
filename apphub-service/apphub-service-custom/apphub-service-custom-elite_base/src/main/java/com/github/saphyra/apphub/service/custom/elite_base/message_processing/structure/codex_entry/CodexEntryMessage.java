package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.codex_entry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class CodexEntryMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("SystemAddress")
    private Long starId;

    @JsonProperty("System")
    private String starName;

    @JsonProperty("StarPos")
    private Double[] starPosition;

    @JsonProperty("BodyID")
    private Long bodyId;

    @JsonProperty("BodyName")
    private String bodyName;

    //Unused
    private Boolean horizons;
    private Boolean odyssey;

    @JsonProperty("Category")
    private String category;

    @JsonProperty("SubCategory")
    private String subCategory;

    @JsonProperty("EntryID")
    private String entityId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Region")
    private String region;

    @JsonProperty("Latitude")
    private Double latitude;

    @JsonProperty("Longitude")
    private Double longitude;

    @JsonProperty("VoucherAmount")
    private Integer voucherAmount;

    @JsonProperty("NearestDestination")
    private String nearestDestination;

    @JsonProperty("Traits")
    private String[] traits;
}
