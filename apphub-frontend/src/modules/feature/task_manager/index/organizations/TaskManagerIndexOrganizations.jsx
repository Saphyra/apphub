import Button from "common/component/input/Button";
import { useState } from "react";
import { TASK_MANAGER_CREATE_ORGANIZATION_PAGE, TASK_MANAGER_GET_ORGANIZATIONS, TASK_MANAGER_ORGANIZATION_INDEX_PAGE } from "../../TaskManagerEndpoints";
import useLoader from "common/hook/Loader";
import Stream from "common/js/collection/Stream";

export const TaskManagerIndexOrganizations = ({ setDisplaySpinner, localizationHandler, refreshCounter }) => {
    const [organizations, setOrganizations] = useState([]);

    useLoader({
        request: TASK_MANAGER_GET_ORGANIZATIONS.createRequest(),
        mapper: setOrganizations,
        setDisplaySpinner: setDisplaySpinner,
        listener: [refreshCounter]
    });

    return (
        <div id="task-manager-index-organizations">
            <div className="task-manager-index-section-title">{localizationHandler.get("organizations")}</div>

            {organizations.length == 0 &&
                <div className="task-manager-index-section-empty">{localizationHandler.get("no-organizations")}</div>
            }

            {organizations.length > 0 &&
                <div id="task-manager-index-organization-list">{getOrganizations()}</div>
            }

            <hr />

            <Button
                id="task-manager-index-create-organization-button"
                label={localizationHandler.get("create-organization")}
                onclick={() => window.location.href = TASK_MANAGER_CREATE_ORGANIZATION_PAGE}
            />
        </div>
    );

    function getOrganizations() {
        return new Stream(organizations)
            .map(organization =>
                <div
                    key={organization.organizationId}
                    className="task-manager-index-organization button"
                    title={organization.description}
                    onClick={() => window.location.href = TASK_MANAGER_ORGANIZATION_INDEX_PAGE.assembleUrl({ organizationId: organization.organizationId })}
                >
                    {organization.organizationName}
                </div>
            )
            .toList();
    }
}

export default TaskManagerIndexOrganizations;