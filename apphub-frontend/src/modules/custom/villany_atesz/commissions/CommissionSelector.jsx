import React from "react";
import SelectInput, { SelectOption } from "../../../../common/component/input/SelectInput";
import { hasValue } from "../../../../common/js/Utils";
import Stream from "../../../../common/js/collection/Stream";
import LocalDateTime from "../../../../common/js/date/LocalDateTime";

const CommissionSelector = ({ localizationHandler, commissions, commissionId, setCommissionId }) => {
    return <SelectInput
        id="villany-atesz-commission-selector"
        value={hasValue(commissionId) ? commissionId : ""}
        options={getOptions()}
        onchangeCallback={setCommissionId}
    />

    function getOptions() {
        let stream = new Stream(commissions)
            .sorted((a, b) => b.lastUpdate.localeCompare(a.lastUpdate))
            .map(commission => new SelectOption(getCommissionName(commission), commission.commissionId))

        if (new Stream(commissions).noneMatch(commission => commission.commissionId == commissionId)) {
            stream = stream.reverse()
                .add(new SelectOption(localizationHandler.get("unknown-commission"), ""))
                .reverse();
        }

        return stream.toList();

        function getCommissionName(commission) {
            const lastUpdate = LocalDateTime.fromLocalDateTime(commission.lastUpdate).formatWithoutSeconds();
            const contactName = (hasValue(commission.contactName) ? commission.contactName : localizationHandler.get("unknown-contact"))

            return lastUpdate + " - " + contactName;
        }
    }
}

export default CommissionSelector;