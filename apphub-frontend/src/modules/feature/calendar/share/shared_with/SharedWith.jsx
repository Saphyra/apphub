import InputField from "common/component/input/InputField";
import Stream from "common/js/collection/Stream";
import { useState } from "react";
import SharedWithUser from "./SharedWithUser";

const SharedWith = ({ localizationHandler, type, sharedWith, setDisplaySpinner, objectId, setConfirmationDialogData, itemName, refresh }) => {
    const [searchText, setSearchText] = useState("");

    return (
        <div id="calendar-shared-with">
            <h2 id="calendar-shared-with-title">{localizationHandler.get("shared-with", { type: localizationHandler.get(type) })}</h2>
            <div>
                <InputField
                    id="calendar-shared-with-search"
                    value={searchText}
                    placeholder={localizationHandler.get("filter-placeholder")}
                    onchangeCallback={setSearchText}
                />
            </div>

            <div id="calendar-shared-with-list">{getSharedWith()}</div>
        </div>
    );

    function getSharedWith() {
        return new Stream(sharedWith)
            .filter(user => user.username.toLowerCase().includes(searchText.toLowerCase()) || user.email.toLowerCase().includes(searchText.toLowerCase()))
            .map(user => <SharedWithUser
                key={user.userId}
                user={user}
                localizationHandler={localizationHandler}
                type={type}
                setDisplaySpinner={setDisplaySpinner}
                objectId={objectId}
                setConfirmationDialogData={setConfirmationDialogData}
                itemName={itemName}
                refresh={refresh}
            />
            )
            .toList();
    }
}

export default SharedWith;