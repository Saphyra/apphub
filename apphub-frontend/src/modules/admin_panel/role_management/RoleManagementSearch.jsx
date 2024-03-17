import React from "react";
import InputField from "../../../common/component/input/InputField";
import Button from "../../../common/component/input/Button";
import Endpoints from "../../../common/js/dao/dao";

const RoleManagementSearch = ({ localizationHandler, query, setQuery, setUsers }) => {
    const search = async () => {
        if (query.length < 3) {
            return;
        }

        const response = await Endpoints.USER_DATA_GET_USER_ROLES.createRequest({ value: query }, {}, { includeSelf: true })
            .send();

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