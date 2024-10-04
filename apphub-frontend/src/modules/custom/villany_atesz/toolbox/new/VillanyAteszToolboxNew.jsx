import React, { useState } from "react";
import LocalDate from "../../../../../common/js/date/LocalDate";
import localizationData from "./villany_atesz_toolbox_new_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import InputField from "../../../../../common/component/input/InputField";
import "./villany_atesz_toolbox_new.css";
import PreLabeledInputField from "../../../../../common/component/input/PreLabeledInputField";
import Button from "../../../../../common/component/input/Button";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import useLoader from "../../../../../common/hook/Loader";
import Stream from "../../../../../common/js/collection/Stream";
import DataListInputField, { DataListInputEntry } from "../../../../../common/component/input/DataListInputField";
import { isBlank } from "../../../../../common/js/Utils";
import { VILLANY_ATESZ_CREATE_TOOL, VILLANY_ATESZ_GET_STORAGE_BOXES, VILLANY_ATESZ_GET_TOOL_TYPES } from "../../../../../common/js/dao/endpoints/VillanyAteszEndpoints";

const VillanyAteszToolboxNew = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [toolType, setToolType] = useState(new DataListInputEntry());
    const [storageBox, setStorageBox] = useState(new DataListInputEntry());
    const [brand, setBrand] = useState("");
    const [name, setName] = useState("");
    const [cost, setCost] = useState(0);
    const [acquiredAt, setAcquiredAt] = useState(LocalDate.now().toString());
    const [warrantyExpiresAt, setWarrantyExpiresAt] = useState("");

    const [toolTypes, setToolTypes] = useState([]);
    const [storageBoxes, setStorageBoxes] = useState([]);
    const [uploadCounter, setUploadCounter] = useState(0);

    useLoader(VILLANY_ATESZ_GET_TOOL_TYPES.createRequest(), result => setToolTypes(new Stream(result).map(item => new DataListInputEntry(item.toolTypeId, item.name)).toList()), [uploadCounter]);
    useLoader(VILLANY_ATESZ_GET_STORAGE_BOXES.createRequest(), result => setStorageBoxes(new Stream(result).map(item => new DataListInputEntry(item.storageBoxId, item.name)).toList()), [uploadCounter]);

    const create = async () => {
        if (isBlank(name)) {
            NotificationService.showError(localizationHandler.get("blank-name"));
            return;
        }

        if (isBlank(acquiredAt)) {
            NotificationService.showError(localizationHandler.get("empty-acquired-at"));
            return;
        }

        const payload = {
            storageBox: {
                storageBoxId: storageBox.key,
                name: storageBox.value
            },
            toolType: {
                toolTypeId: toolType.key,
                name: toolType.value
            },
            brand: brand,
            name: name,
            cost: cost,
            acquiredAt: acquiredAt,
            warrantyExpiresAt: warrantyExpiresAt
        };

        await VILLANY_ATESZ_CREATE_TOOL.createRequest(payload)
            .send();

        setBrand("");
        setName("");
        setCost(0);
        setWarrantyExpiresAt("");
        setToolType(new DataListInputEntry());
        setStorageBox(new DataListInputEntry());
        setUploadCounter(uploadCounter + 1);

        NotificationService.showSuccess(localizationHandler.get("tool-created"));
    }

    return (
        <div id="villany-atesz-toolbox-new">
            <div className="villany-atesz-toolbox-new-fieldset-wrapper">
                <fieldset>
                    <legend>{localizationHandler.get("general")}</legend>

                    <PreLabeledInputField
                        label={localizationHandler.get("tool-type")}
                        input={<DataListInputField
                            id="villany-atesz-toolbox-new-tool-type"
                            value={toolType}
                            setValue={setToolType}
                            options={toolTypes}
                            placeholder={localizationHandler.get("tool-type")}
                        />
                        }
                    />

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

                    <PreLabeledInputField
                        label={localizationHandler.get("storage-box")}
                        input={<DataListInputField
                            id="villany-atesz-toolbox-new-storage-box"
                            value={storageBox}
                            setValue={setStorageBox}
                            options={storageBoxes}
                            placeholder={localizationHandler.get("storage-box")}
                        />
                        }
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