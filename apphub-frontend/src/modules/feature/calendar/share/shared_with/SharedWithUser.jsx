import { MultiSelect, SelectOption } from "common/component/input/SelectInput";
import useCache from "common/hook/Cache";
import Stream from "common/js/collection/Stream";
import { useState } from "react";
import { CALENDAR_GET_GRANTS, CALENDAR_SHARE_EDIT_GRANTS, CALENDAR_UNSHARE_OBJECT } from "../../CalendarEndpoints";
import Button from "common/component/input/Button";
import NotificationService from "common/js/notification/NotificationService";
import ConfirmationDialogData from "common/component/confirmation_dialog/ConfirmationDialogData";

const SharedWithUser = ({ user, localizationHandler, type, setDisplaySpinner, objectId, setConfirmationDialogData, itemName, refresh }) => {
    const [grants, setGrants] = useState([]);
    const [selectedGrants, setSelectedGrants] = useState(user.grants);

    useCache("calendar-share-grants-" + type, CALENDAR_GET_GRANTS.createRequest(null, { type, type }), setGrants);

    return (
        <div className="shared-with-user">
            <div className="shared-with-user-title">
                <div>{user.username}</div>
                <div>{user.email}</div>
            </div>

            <div>
                <MultiSelect
                    id="calendar-share-with-selected-user-grants"
                    value={selectedGrants}
                    onchangeCallback={setSelectedGrants}
                    options={getOperationOptions()}
                />
            </div>

            <div className="calendar-share-with-selected-user-grants">
                <Button
                    className="shared-with-user-save-button"
                    label={localizationHandler.get("save")}
                    onclick={saveGrants}
                />

                <Button
                    className="shared-with-user-unshare-button"
                    label={localizationHandler.get("unshare")}
                    onclick={confirmUnshare}
                />
            </div>
        </div>
    );

    function getOperationOptions() {
        return new Stream(grants)
            .map(operation => new SelectOption(localizationHandler.get(operation, { type: localizationHandler.get(type) }), operation))
            .toList();
    }

    async function saveGrants() {
        await CALENDAR_SHARE_EDIT_GRANTS.createRequest(selectedGrants, { type: type, id: objectId, sharedWith: user.userId })
            .send(setDisplaySpinner);

        NotificationService.showSuccess(localizationHandler.get("grants-saved"))
    }

    function confirmUnshare() {
        setConfirmationDialogData(new ConfirmationDialogData(
            "calendar-share-unshare-confirmation-dialog",
            localizationHandler.get("unshare-confirmation-title", { type: localizationHandler.get(type) }),
            localizationHandler.get(
                "unshare-confirmation-detail",
                {
                    username: user.username,
                    email: user.email,
                    type: localizationHandler.get(type),
                    name: itemName
                }
            ),
            [
                <Button
                    key="unshare"
                    label={localizationHandler.get("unshare")}
                    onclick={unshare}
                />,
                <Button
                    key="cancel"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));

        async function unshare() {
            await CALENDAR_UNSHARE_OBJECT.createRequest(null, { type: type, id: objectId, sharedWith: user.userId })
                .send(setDisplaySpinner);

            refresh();
            setConfirmationDialogData(null);
        }
    }
}

export default SharedWithUser;