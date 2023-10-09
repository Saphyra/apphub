package com.github.saphyra.apphub.service.notebook.service.custom_table_deprecated.data_type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Number {
    private Double step;
    private Double value;
}
