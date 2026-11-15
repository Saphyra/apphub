import Button from "common/component/input/Button";
import LocalizationHandler from "common/js/LocalizationHandler";
import InputField from "common/component/input/InputField";
import Stream from "common/js/collection/Stream";
import { USER_DATA_SEARCH_ACCOUNT } from "common/js/GenericEndpoints";
import NotificationService from "common/js/notification/NotificationService";
import { hasValue } from "common/js/Utils";
import { useState } from "react";
import { CALENDAR_GET_GRANTS } from "../../CalendarEndpoints";
import useCache from "common/hook/Cache";
import { MultiSelect, SelectOption } from "common/component/input/SelectInput";
import localizationData from "./share_with_localization.json";
import grantLocalizationData from "./grant_localization.json";

const ShareWith = ({ type, setDisplaySpinner, shareCallback, existingUserIds = [] }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const grantLocalizationHandler = new LocalizationHandler(grantLocalizationData);

    const [searchText, setSearchText] = useState("");
    const [searchResult, setSearchResult] = useState(null);
    const [selectedUser, setSelectedUser] = useState(null);
    const [grants, setGrants] = useState([]);
    const [selectedGrants, setSelectedGrants] = useState([]);

    useCache("calendar-share-grants-" + type, CALENDAR_GET_GRANTS.createRequest(null, { type, type }), setGrants);

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
                        id="calendar-share-with-selected-user-grants"
                        value={selectedGrants}
                        onchangeCallback={setSelectedGrants}
                        options={getGrantOptions()}
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

        const filtered = response.filter(user => !existingUserIds.includes(user.userId));
        setSearchResult(filtered);
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

function getGrantOptions() {
    return new Stream(grants)
        .map(grant => new SelectOption(grantLocalizationHandler.get(grant, { type: localizationHandler.get(type) }), grant))
        .toList();
}

async function share() {
    shareCallback(selectedUser.userId, selectedGrants, selectedUser);
    setSelectedUser(null);
    setSelectedGrants([]);
    setSearchText("");
}
}

export default ShareWith;