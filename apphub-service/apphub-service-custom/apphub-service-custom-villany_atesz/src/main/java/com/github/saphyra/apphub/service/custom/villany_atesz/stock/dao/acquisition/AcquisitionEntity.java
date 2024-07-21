package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "villany_atesz", name = "acquisition")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
class AcquisitionEntity {
    @Id
    private String acquisitionId;
    private String userId;
    private String acquiredAt; //LocalDate
    private String stockItemId;
    private String amount;
}
