package com.github.saphyra.apphub.integration.structure.notebook;

import com.github.saphyra.apphub.integration.structure.KeyValuePair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EditTableRequest {
    private String title;
    private List<KeyValuePair<String>> columnNames;
    private List<List<KeyValuePair<String>>> columns;
}
