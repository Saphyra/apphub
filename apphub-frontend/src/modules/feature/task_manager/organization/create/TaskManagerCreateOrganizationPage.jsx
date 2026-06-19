import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./task_manager_create_organization_page_localization.json";
import { useEffect, useState } from "react";
import NotificationService from "common/js/notification/NotificationService";
import Header from "common/component/Header";
import Footer from "common/component/Footer";
import { TASK_MANAGER_CREATE_ORGANIZATION, TASK_MANAGER_PAGE } from "../../TaskManagerEndpoints";
import ConfirmationDialog from "common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "common/component/Spinner";
import { ToastContainer } from "react-toastify";
import Button from "common/component/input/Button";
import InputField from "common/component/input/InputField";
import "./task_manager_create_organization_page.css";
import Textarea from "common/component/input/Textarea";
import { USER_DATA_SEARCH_ACCOUNT } from "common/js/GenericEndpoints";
import Constants from "common/js/Constants";
import { hasValue, isBlank, removeAndSet } from "common/js/Utils";
import Stream from "common/js/collection/Stream";

const TaskManagerCreateOrganizationPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(0);

    const [organizationName, setOrganizationName] = useState("");
    const [description, setDescription] = useState("");
    const [userSearch, setUserSearch] = useState("");
    const [userSearchResult, setUserSearchResult] = useState(null);
    const [invitedUsers, setInvitedUsers] = useState([]);

    useEffect(() => NotificationService.displayStoredMessages(), []);

    const updateDisplaySpinner = (display) => {
        setDisplaySpinner(prev => prev + (display ? 1 : -1));
    }

    return (
        <div id="task-manager-create-organization" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="task-manager-create-organization-main">
                <fieldset>
                    <legend>{localizationHandler.get("organization-name")}</legend>

                    <InputField
                        id="task-manager-create-organization-name"
                        label={localizationHandler.get("organization-name")}
                        placeholder={localizationHandler.get("organization-name")}
                        value={organizationName}
                        onchangeCallback={setOrganizationName}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("organization-description")}</legend>

                    <Textarea
                        id="task-manager-create-organization-description"
                        placeholder={localizationHandler.get("organization-description")}
                        value={description}
                        onchangeCallback={setDescription}
                        onKeyDownCallback={e => {
                            if (e.key === "Tab") {
                                e.preventDefault();

                                const start = e.target.selectionStart;
                                const end = e.target.selectionEnd;
                                e.target.value = e.target.value.substring(0, start) + "    " + e.target.value.substring(end);
                                e.target.selectionStart = e.target.selectionEnd = start + 4;
                            }
                        }}
                        onKeyUpCallback={e => {
                            e.target.style.height = "auto";
                            e.target.style.height = e.target.scrollHeight + 6 + "px";
                        }}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("invited-users")}</legend>

                    {invitedUsers.length == 0 &&
                        <div id="task-manager-create-organization-no-invited-users">{localizationHandler.get("no-invited-users")}</div>
                    }

                    {invitedUsers.length > 0 &&
                        <div id="task-manager-create-organization-invited-users">
                            {getInvitedUsers()}
                        </div>
                    }

                    <InputField
                        id="task-manager-create-organization-user-search-input"
                        placeholder={localizationHandler.get("user-search")}
                        value={userSearch}
                        onchangeCallback={setUserSearch}
                    />

                    <Button
                        id="task-manager-create-organization-user-search-button"
                        label={localizationHandler.get("search")}
                        onclick={searchUsers}
                    />

                    {hasValue(userSearchResult) && userSearchResult.length == 0 &&
                        <div id="task-manager-create-organization-no-user-found">{localizationHandler.get("no-user-found")}</div>
                    }

                    {hasValue(userSearchResult) && userSearchResult.length > 0 &&
                        <div id="task-manager-create-organization-user-search-results">
                            {getCandidates()}
                        </div>
                    }
                </fieldset>
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="task-manager-create-organization-back-button"
                        onclick={() => window.location.href = TASK_MANAGER_PAGE}
                        label={localizationHandler.get("back")}
                    />
                }
                centerButtons={
                    <Button
                        id="task-manager-create-organization-create-button"
                        onclick={createOrganization}
                        label={localizationHandler.get("create")}
                    />
                }
            />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            {displaySpinner > 0 && <Spinner />}

            <ToastContainer />
        </div>
    );

    function getInvitedUsers() {
        return new Stream(invitedUsers)
            .map(user => (
                <div
                    key={user.userId}
                    className="task-manager-create-organization-user"
                >
                    <span className="task-manager-create-organization-user-name">{user.username}</span>
                    <span>: </span>
                    (<span className="task-manager-create-organization-user-email">{user.email}</span>)

                    <Button
                        className="task-manager-create-organization-user-invite-button"
                        label="X"
                        onclick={() => removeAndSet(invitedUsers, u => u.userId == user.userId, setInvitedUsers)}
                    />
                </div>
            ))
            .toList();
    }

    function getCandidates() {
        return new Stream(userSearchResult)
            .map(user => (
                <div
                    key={user.userId}
                    className="task-manager-create-organization-user"
                >
                    <span className="task-manager-create-organization-user-name">{user.username}</span>
                    <span>: </span>
                    (<span className="task-manager-create-organization-user-email">{user.email}</span>)

                    <Button
                        className="task-manager-create-organization-user-invite-button"
                        label="+"
                        onclick={() => {
                            invitedUsers.push(user);
                            setUserSearchResult(null);
                        }}
                    />
                </div>
            ))
            .toList();
    }

    async function searchUsers() {
        if (userSearch.length < Constants.MIN_USER_SEARCH_LENGTH) {
            NotificationService.showError(localizationHandler.get("user-search-too-short"));
            return;
        }

        const response = await USER_DATA_SEARCH_ACCOUNT.createRequest({ value: userSearch })
            .send(updateDisplaySpinner);

        const ivitedUserIds = new Stream(invitedUsers)
            .map(user => user.userId)
            .toList();

        const users = new Stream(response)
            .filter(user => !ivitedUserIds.includes(user.userId))
            .toList();

        setUserSearchResult(users);
    }

    async function createOrganization() {
        //TODO limit max length of organizationName and organizationDescription
        if(isBlank(organizationName)) {
            NotificationService.showError(localizationHandler.get("organization-name-required"));
            return;
        }

        const payload = {
            organizationName: organizationName,
            description: description,
            invitedUsers: invitedUsers.map(user => user.userId)
        }

        await TASK_MANAGER_CREATE_ORGANIZATION.createRequest(payload)
            .send(updateDisplaySpinner);

        window.location.href = TASK_MANAGER_PAGE;
    }
}

export default TaskManagerCreateOrganizationPage;