import React from "react";
import Stream from "../../../../common/js/collection/Stream";
import RoleManagementSearchResultUser from "./RoleManagementSearchResultUser";

const RoleManagementSearchResult = ({ localizationHandler, users, setUsers, query }) => {
    if (users.length == 0) {
        return (
            <div id="role-management-no-search-result">
                {localizationHandler.get("no-search-result")}
            </div>
        )
    }

    const getUsers = () => {
        return new Stream(users)
            .map(user => <RoleManagementSearchResultUser
                key={user.userId}
                localizationHandler={localizationHandler}
                user={user}
                users={users}
                setUsers={setUsers}
                query={query}
            />)
            .toList();
    }

    return (
        <div id="role-management-search-result">
            <table id="role-management-search-result-table" className="formatted-table">
                <thead>
                    <tr>
                        <th>{localizationHandler.get("username")}</th>
                        <th>{localizationHandler.get("email")}</th>
                        <th>{localizationHandler.get("granted-roles")}</th>
                        <th>{localizationHandler.get("available-roles")}</th>
                    </tr>
                </thead>
                <tbody>
                    {getUsers()}
                </tbody>
            </table>

        </div>
    );
}

export default RoleManagementSearchResult;