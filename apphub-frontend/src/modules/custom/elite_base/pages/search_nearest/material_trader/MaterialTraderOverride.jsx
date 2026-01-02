import React, { useState } from "react";
import { hasValue } from "../../../../../../common/js/Utils";
import Button from "../../../../../../common/component/input/Button";
import SelectInput, { SelectOption } from "../../../../../../common/component/input/SelectInput";
import MaterialType from "./MaterialType";
import MapStream from "../../../../../../common/js/collection/MapStream";
import ConfirmationDialog from "../../../../../../common/component/confirmation_dialog/ConfirmationDialog";
import { ELITE_BASE_IS_ADMIN, ELITE_BASE_MATERIAL_TRADER_OVERRIDE_CREATE, ELITE_BASE_MATERIAL_TRADER_OVERRIDE_DELETE, ELITE_BASE_MATERIAL_TRADER_OVERRIDE_VERIFY } from "../../../common/EliteBaseEndpoints";
import useCache from "../../../../../../common/hook/Cache";

const MaterialTraderOverride = ({ localizationHandler, record, reload }) => {
    const [isAdmin, setIsAdmin] = useState(false);

    useCache("elite-base-is-admin", ELITE_BASE_IS_ADMIN.createRequest(), setIsAdmin);

    if (record.verifiedMaterialOverride) {
        return (
            <span
                className="float-right"
                title={localizationHandler.get("overridden-material-trader-type", { value: record.originalMaterialType })}
            >
                &#10069;
            </span>
        );
    } else if (!hasValue(record.verifiedMaterialOverride)) {
        return <NoOverride
            localizationHandler={localizationHandler}
            record={record}
            reload={reload}
        />
    } else {
        if (isAdmin) {
            return <UnverifiedOverride
                localizationHandler={localizationHandler}
                record={record}
                reload={reload}
            />
        } else {
            return (
                <span
                    className="float-right"
                    title={localizationHandler.get("unverified-overridden-material-trader-type", { value: record.originalMaterialType })}
                >
                    &#10071;
                </span>
            );
        }
    }
}

const NoOverride = ({ localizationHandler, record, reload }) => {
    const [displaySelector, setDisplaySelector] = useState(false);
    const [materialOverride, setMaterialOverride] = useState(record.materialType);

    return (
        <span>
            <Button
                className="elite-base-nearest-material-trader-result-override-button"
                label="&#128295;"
                title={localizationHandler.get("edit-material-trader-override")}
                onclick={() => setDisplaySelector(true)}
            />

            {displaySelector &&
                <ConfirmationDialog
                    id="elite-base-nearest-material-trader-override-dialog"
                    title={localizationHandler.get("edit-material-trader-override-dialog-title")}
                    content={<SelectInput
                        value={materialOverride}
                        onchangeCallback={setMaterialOverride}
                        options={getOptions()}
                    />}
                    choices={[
                        <Button
                            key="confirm"
                            label={localizationHandler.get("report-misinformation")}
                            onclick={createMaterialTraderOverride}
                        />,
                        <Button
                            key="cancel"
                            label={localizationHandler.get("cancel")}
                            onclick={() => setDisplaySelector(false)}
                        />
                    ]}
                />
            }
        </span>
    );

    function getOptions() {
        return new MapStream(MaterialType)
            .toListStream()
            .add("NONE")
            .map(materialType => new SelectOption(materialType, materialType))
            .toList();
    }

    async function createMaterialTraderOverride() {
        await ELITE_BASE_MATERIAL_TRADER_OVERRIDE_CREATE.createRequest({ stationId: record.stationId, materialType: materialOverride })
            .send();

        setDisplaySelector(false);
        reload();
    }
}

const UnverifiedOverride = ({ localizationHandler, record, reload }) => {
    const [displaySelector, setDisplaySelector] = useState(false);

    return (
        <span>
            <Button
                className="elite-base-nearest-material-trader-result-override-button"
                label="&#10067;"
                title={localizationHandler.get("edit-material-trader-override")}
                onclick={() => setDisplaySelector(true)}
            />

            {displaySelector &&
                <ConfirmationDialog
                    id="elite-base-nearest-material-trader-override-dialog"
                    title={localizationHandler.get("verify-material-trader-override-dialog-title")}
                    content={localizationHandler.get("verify-material-trader-override-dialog-detail", { original: record.originalMaterialType, override: record.materialType })}
                    choices={[
                        <Button
                            key="verify"
                            label={localizationHandler.get("verify-material-trader-override")}
                            onclick={verifyMaterialTraderOverride}
                        />,
                        <Button
                            key="delete"
                            label={localizationHandler.get("delete-material-trader-override")}
                            onclick={deleteMaterialTraderOverride}
                        />,
                        <Button
                            key="cancel"
                            label={localizationHandler.get("cancel")}
                            onclick={() => setDisplaySelector(false)}
                        />
                    ]}
                />
            }
        </span>
    );

    async function verifyMaterialTraderOverride() {
        await ELITE_BASE_MATERIAL_TRADER_OVERRIDE_VERIFY.createRequest(null, { stationId: record.stationId })
            .send();

        reload();
    }

    async function deleteMaterialTraderOverride() {
        await ELITE_BASE_MATERIAL_TRADER_OVERRIDE_DELETE.createRequest(null, { stationId: record.stationId })
            .send();

        reload();
    }
}

export default MaterialTraderOverride;