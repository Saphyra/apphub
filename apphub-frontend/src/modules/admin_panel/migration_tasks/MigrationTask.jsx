import React from "react";
import InputField from "../../../common/component/input/InputField";
import Button from "../../../common/component/input/Button";

const MigrationTask = ({ localizationHandler, data, deleteCallback, triggerCallback }) => {
    return (
        <tr className="migration-task">
            <td>{data.event}</td>
            <td>{data.name}</td>
            <td className="migration-task-checkbox-cell">
                <InputField
                    type="checkbox"
                    checked={data.completed}
                    disabled={true}
                />
            </td>
            <td>
                <Button
                    className="migration-task-delete-button"
                    label={localizationHandler.get("delete")}
                    onclick={deleteCallback}
                />

                {
                    !data.completed &&
                    
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