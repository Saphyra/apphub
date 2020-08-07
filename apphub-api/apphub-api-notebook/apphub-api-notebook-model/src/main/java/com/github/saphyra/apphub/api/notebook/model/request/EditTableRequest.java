package com.github.saphyra.apphub.api.notebook.model.request;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
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
    private List<KeyValuePair<String>> columnNames;
    private List<List<KeyValuePair<String>>> columns;
}
