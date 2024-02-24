package com.github.saphyra.apphub.api.notebook.model.pin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PinGroupResponse {
    private UUID pinGroupId;
    private String pinGroupName;
}
