import Button from "common/component/input/Button";
import InputField from "common/component/input/InputField";
import React from "react";
import { USER_DATA_GET_USER_ROLES } from "../AdminPanelEndpoints";

const RoleManagementSearch = ({ localizationHandler, query, setQuery, setUsers, setDisplaySpinner }) => {
    const search = async () => {
        if (query.length < 3) {
            return;
        }

        const response = await USER_DATA_GET_USER_ROLES.createRequest({ value: query }, {}, { includeSelf: true })
            .send(setDisplaySpinner);

        setUsers(response);
    }

    return (
        <div id="role-management-search">
            <InputField
                id="role-management-search-input"
                placeholder={localizationHandler.get("search")}
                value={query}
                onchangeCallback={setQuery}
            />

            <Button
                id="role-management-search-button"
                label={localizationHandler.get("search")}
                disabled={query.length < 3}
                onclick={search}
            />
        </div>
    );
}

export default RoleManagementSearch;