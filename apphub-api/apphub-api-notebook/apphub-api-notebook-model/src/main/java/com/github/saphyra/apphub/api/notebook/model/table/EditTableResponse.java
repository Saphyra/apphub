package com.github.saphyra.apphub.api.notebook.model.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class EditTableResponse {
    private TableResponse tableResponse;
    private List<TableFileUploadResponse> fileUpload;
}
