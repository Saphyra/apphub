import React from "react";
import InputField from "../../../common/component/input/InputField";
import Button from "../../../common/component/input/Button";

const MigrationTask = ({ localizationHandler, data, deleteCallback, triggerCallback }) => {
    return (
        <tr className="migration-task">
            <td className="migration-task-event">{data.event}</td>
            <td className="migration-task-name">{data.name}</td>
            <td className="migration-task-checkbox-cell">
                <InputField
                    type="checkbox"
                    className={"migration-task-completed"}
                    checked={data.completed}
                    disabled={true}
                />
            </td>
            <td className="migration-task-commands">
                {
                    !data.repeatable &&
                    <Button
                        className="migration-task-delete-button"
                        label={localizationHandler.get("delete")}
                        onclick={deleteCallback}
                    />
                }

                {
                    (!data.completed || data.repeatable) &&

                    <Button
                        className="migration-task-trigger-button"
                        label={localizationHandler.get("trigger")}
                        onclick={triggerCallback}
                    />
                }

            </td>
        </tr>
    );
}

export default MigrationTask;