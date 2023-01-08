package com.github.saphyra.apphub.api.platform.storage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateFileRequest {
    private String extension;
    private Integer size;
}
