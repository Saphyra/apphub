import React, { useState } from "react";
import LocalDate from "../../../../../common/js/date/LocalDate";
import localizationData from "./villany_atesz_toolbox_new_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import InputField from "../../../../../common/component/input/InputField";
import "./villany_atesz_toolbox_new.css";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import Button from "../../../../../common/component/input/Button";
import Utils from "../../../../../common/js/Utils";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import Endpoints from "../../../../../common/js/dao/dao";

const VillanyAteszToolboxNew = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [brand, setBrand] = useState("");
    const [name, setName] = useState("");
    const [cost, setCost] = useState(0);
    const [acquiredAt, setAcquiredAt] = useState(LocalDate.now().toString());
    const [warrantyExpiresAt, setWarrantyExpiresAt] = useState("");

    const create = async () => {
        if (Utils.isBlank(name)) {
            NotificationService.showError(localizationHandler.get("blank-name"));
            return;
        }

        if (Utils.isBlank(acquiredAt)) {
            NotificationService.showError(localizationHandler.get("empty-acquired-at"));
            return;
        }


        const payload = {
            brand: brand,
            name: name,
            cost: cost,
            acquiredAt: acquiredAt,
            warrantyExpiresAt: warrantyExpiresAt
        };

        await Endpoints.VILLANY_ATESZ_CREATE_TOOL.createRequest(payload)
            .send();

        setBrand("");
        setName("");
        setCost("");
        setWarrantyExpiresAt("");

        NotificationService.showSuccess(localizationHandler.get("tool-created"));
    }

    return (
        <div id="villany-atesz-toolbox-new">
            <div className="villany-atesz-toolbox-new-fieldset-wrapper">
                <fieldset>
                    <legend>{localizationHandler.get("general")}</legend>

                    <PreLabeledInputField
                        label={localizationHandler.get("brand")}
                        input={<InputField
                            id="villany-atesz-toolbox-new-brand"
                            placeholder={localizationHandler.get("brand")}
                            value={brand}
                            onchangeCallback={setBrand}
                            style={{
                                width: 8 * brand.length + "px",
                                minWidth: "200px"
                            }}
                        />}
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("name")}
                        input={<InputField
                            id="villany-atesz-toolbox-new-name"
                            placeholder={localizationHandler.get("name")}
                            value={name}
                            onchangeCallback={setName}
                            style={{
                                width: 8 * name.length + "px",
                                minWidth: "200px"
                            }}
                        />}
                    />
                </fieldset>
            </div>

            <div className="villany-atesz-toolbox-new-fieldset-wrapper">
                <fieldset>
                    <legend>{localizationHandler.get("acquisition")}</legend>

                    <PreLabeledInputField
                        label={localizationHandler.get("cost")}
                        input={<InputField
                            id="villany-atesz-toolbox-new-cost"
                            type="number"
                            placeholder={localizationHandler.get("cost")}
                            value={cost}
                            onchangeCallback={setCost}
                        />}
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("acquired-at")}
                        input={<InputField
                            id="villany-atesz-toolbox-new-acquired-at"
                            type="date"
                            placeholder={localizationHandler.get("acquired-at")}
                            value={acquiredAt}
                            onchangeCallback={setAcquiredAt}
                        />}
                    />

                    <PreLabeledInputField
                        label={localizationHandler.get("warranty-expires-at")}
                        input={<InputField
                            id="villany-atesz-toolbox-new-warranty-expires-at"
                            type="date"
                            placeholder={localizationHandler.get("warranty-expires-at")}
                            value={warrantyExpiresAt}
                            onchangeCallback={setWarrantyExpiresAt}
                        />}
                    />
                </fieldset>
            </div>

            <Button
                id="villany-atesz-toolbox-new-create"
                label={localizationHandler.get("create")}
                onclick={create}
            />
        </div>
    );
}

export default VillanyAteszToolboxNew;