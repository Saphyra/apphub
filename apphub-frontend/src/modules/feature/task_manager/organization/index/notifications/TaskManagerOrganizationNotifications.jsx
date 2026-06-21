import useLoader from "common/hook/Loader";
import Stream from "common/js/collection/Stream";
import { hasValue } from "common/js/Utils";
import { TASK_MANAGER_GET_NOTIFICATIONS } from "modules/feature/task_manager/TaskManagerEndpoints";
import { NotificationStatus } from "./NotificationStatus";
import Notification from "./notification/Notification";
import notificationComparator from "../NotificationComparator";
import useRefresh from "common/hook/Refresh";
import { useState } from "react";

const TaskManagerOrganizationNotifications = ({ localizationHandler, setDisplaySpinner }) => {
    const [refreshCounter, refresh] = useRefresh();

    const [notifications] = useLoader({
        request: TASK_MANAGER_GET_NOTIFICATIONS.createRequest(),
        setDisplaySpinner: setDisplaySpinner,
        listener: [refreshCounter],
    });

    const unreadNotificationCount = hasValue(notifications) ? new Stream(notifications).filter(notification => notification.status != NotificationStatus.READ).count() : 0;

    if (hasValue(notifications) && notifications.length > 0) {
        return (
            <div className="task-manager-organization-notifications task-manager-organization-index-panel">
                <div className="task-manager-organization-index-panel-header">
                    <span className={unreadNotificationCount > 0 ? "red" : ""}>{unreadNotificationCount}</span>
                </div>

                <div className="task-manager-organization-index-panel-content">{getNotifications()}</div>
            </div>
        );
    }

    function getNotifications() {
        return new Stream(notifications)
            .sorted(notificationComparator)
            .map(notification => <Notification
                key={notification.notificationId}
                notification={notification}
                refresh={refresh}
                setDisplaySpinner={setDisplaySpinner}
            />)
            .toList();
    }
}

export default TaskManagerOrganizationNotifications;