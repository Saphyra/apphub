package com.github.saphyra.apphub.integration.structure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class KeyValuePair<T> {
    private UUID key;
    private T value;
}
