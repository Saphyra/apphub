import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./notification_localization.json";
import "./notification.css";
import InputField from "common/component/input/InputField";
import Button from "common/component/input/Button";
import { NotificationStatus } from "../NotificationStatus";
import { TASK_MANAGER_DELETE_NOTIFICATION, TASK_MANAGER_SET_NOTIFICATION_STATUS } from "modules/feature/task_manager/TaskManagerEndpoints";

const Notification = ({ notification, refresh, setDisplaySpinner }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div className={"task-manager-organization-notificaation " + notification.status.toLowerCase() + " " + notification.type}>
            <InputField
                type="checkbox"
                className="task-manager-organization-notification-checkbox"
            />

            <span className="task-manager-organization-notification-message">{localizationHandler.get(notification.type, notification.data)}</span>

            <div className="task-manager-organization-notification-buttons">
                {notification.status !== NotificationStatus.UNREAD &&
                    <Button
                        className="task-manager-organization-notification-mark-as-unread-button"
                        title={localizationHandler.get("mark-as-unread")}
                        onclick={() => mark(NotificationStatus.UNREAD)}
                    />
                }

                {notification.status !== NotificationStatus.READ &&
                    <Button
                        className="task-manager-organization-notification-mark-as-read-button"
                        title={localizationHandler.get("mark-as-read")}
                        onclick={() => mark(NotificationStatus.READ)}
                    />
                }

                {notification.status !== NotificationStatus.MARKED &&
                    <Button
                        className="task-manager-organization-notification-mark-button"
                        title={localizationHandler.get("mark")}
                        onclick={() => mark(NotificationStatus.MARKED)}
                    />
                }

                <Button
                    className="task-manager-organization-notification-delete-button"
                    title={localizationHandler.get("delete")}
                    onclick={() => deleteNotification()}
                />
            </div>
        </div>
    );

    async function mark(status) {
        await TASK_MANAGER_SET_NOTIFICATION_STATUS.createRequest({ status: status, notificationIds: [notification.notificationId] })
            .send(setDisplaySpinner);

        refresh();
    }

    async function deleteNotification() {
        await TASK_MANAGER_DELETE_NOTIFICATION.createRequest([notification.notificationId])
            .send(setDisplaySpinner);

        refresh();
    }
}

export default Notification;