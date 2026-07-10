import Button from "common/component/input/Button";
import InputField from "common/component/input/InputField";
import Stream from "common/js/collection/Stream";
import { USER_DATA_SEARCH_ACCOUNT } from "common/js/GenericEndpoints";
import NotificationService from "common/js/notification/NotificationService";
import { hasValue } from "common/js/Utils";
import { useState } from "react";
import { CALENDAR_GET_OPERATIONS, CALENDAR_SHARE_OBJECT } from "../../CalendarEndpoints";
import useCache from "common/hook/Cache";
import { MultiSelect, SelectOption } from "common/component/input/SelectInput";

const ShareWith = ({ localizationHandler, type, setDisplaySpinner, objectData, refresh }) => {
    const [searchText, setSearchText] = useState("");
    const [searchResult, setSearchResult] = useState(null);
    const [selectedUser, setSelectedUser] = useState(null);
    const [operations, setOperations] = useState([]);
    const [selectedOperations, setSelectedOperations] = useState([]);

    useCache("calendar-share-operations-" + type, CALENDAR_GET_OPERATIONS.createRequest(null, { type, type }), setOperations);

    return (
        <div id="calendar-share-with">
            <div id="calendar-share-with-inputs">
                <InputField
                    id="calendar-share-with-search-input"
                    placeholder={localizationHandler.get("search-placeholder", { type: localizationHandler.get(type) })}
                    value={searchText}
                    onchangeCallback={setSearchText}
                />

                <Button
                    id="calendar-share-with-search-button"
                    label={localizationHandler.get("search")}
                    onclick={search}
                />
            </div>

            <div id="calendar-share-with-search-result">
                {hasValue(searchResult) && searchResult.length === 0 &&
                    <div id="calendar-share-with-search-result-empty">{localizationHandler.get("search-result-empty")}</div>
                }

                {hasValue(searchResult) && searchResult.length > 0 && getSearchResultList()}
            </div>

            {hasValue(selectedUser) &&
                <div id="calendar-share-with-selected-user">
                    <div id="calendar-share-with-selected-user-title">{localizationHandler.get("share-with-user-title", { username: selectedUser.username, email: selectedUser.email })}</div>

                    <MultiSelect
                        id="calendar-share-with-selected-user-operations"
                        value={selectedOperations}
                        onchangeCallback={setSelectedOperations}
                        options={getOperationOptions()}
                    />

                    <div>
                        <Button
                            id="calendar-share-with-selected-user-share-button"
                            label={localizationHandler.get("share")}
                            onclick={share}
                        />
                    </div>
                </div>
            }
        </div>
    );

    async function search() {
        if (searchText.length < 3) {
            NotificationService.showError(localizationHandler.get("search-text-too-short"));
            return;
        }

        const response = await USER_DATA_SEARCH_ACCOUNT.createRequest({ value: searchText })
            .send(setDisplaySpinner);

        setSearchResult(response);
    }

    function getSearchResultList() {
        return new Stream(searchResult)
            .map(user => <Button
                key={user.userId}
                className="calendar-share-with-search-result-item"
                label={user.username + " (" + user.email + ")"}
                onclick={() => selectUser(user)}
            />
            )
            .toList();
    }

    function selectUser(user) {
        setSelectedUser(user);
        setSearchResult(null);
    }

    function getOperationOptions() {
        return new Stream(operations)
            .map(operation => new SelectOption(localizationHandler.get(operation, { type: localizationHandler.get(type) }), operation))
            .toList();
    }

    async function share() {
        const payload = {
            sharedWith: selectedUser.userId,
            owner: objectData.owner,
            objectId: objectData.objectId,
            type: type,
            operations: selectedOperations
        }

        await CALENDAR_SHARE_OBJECT.createRequest(payload)
            .send(setDisplaySpinner);

        refresh();
        setSelectedUser(null);
        setSelectedOperations([]);
        setSearchText("");
    }
}

export default ShareWith;