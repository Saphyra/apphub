import React from "react";
import Stream from "../../../common/js/collection/Stream";
import Button from "../../../common/component/input/Button";
import ConfirmationDialogData from "../../../common/component/confirmation_dialog/ConfirmationDialogData";
import { ADMIN_PANEL_PERFORMANCE_REPORTING_DISABLE_TOPIC, ADMIN_PANEL_PERFORMANCE_REPORTING_ENABLE_TOPIC } from "../../../common/js/dao/endpoints/AdminPanelEndpoints";

const PerformanceReportingTopics = ({ topics, setTopics, localizationHandler, setConfirmationDialogData }) => {
    const getContent = () => {
        return new Stream(topics)
            .map(topic =>
                <Button
                    key={topic.topic}
                    className={"performance-reporting-topic " + (topic.enabled ? "enabled" : "disabled")}
                    label={topic.topic}
                    onclick={(topic.enabled ? () => confirmDisableTopic(topic) : () => confirmEnableTopic(topic))}
                />
            )
            .toList();
    }

    const confirmEnableTopic = (topic) => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "performance-reporting-enable-topic-confirmation",
            localizationHandler.get("confirm-enable-topic-title"),
            localizationHandler.get("confirm-enable-topic-detail", { topic: topic.topic }),
            [
                <Button
                    key="confirm"
                    className="performance-reporting-enable-topic-confirm-button"
                    label={localizationHandler.get("enable")}
                    onclick={() => enableTopic(topic)}
                />,
                <Button
                    key="cancel"
                    className="performance-reporting-enable-topic-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const confirmDisableTopic = (topic) => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "performance-reporting-disable-topic-confirmation",
            localizationHandler.get("confirm-disable-topic-title"),
            localizationHandler.get("confirm-disable-topic-detail", { topic: topic.topic }),
            [
                <Button
                    key="confirm"
                    className="performance-reporting-disable-topic-confirm-button"
                    label={localizationHandler.get("disable")}
                    onclick={() => disableTopic(topic)}
                />,
                <Button
                    key="extend"
                    className="performance-reporting-extend-topic-button"
                    label={localizationHandler.get("extend")}
                    onclick={() => enableTopic(topic)}
                />,
                <Button
                    key="cancel"
                    className="performance-reporting-disable-topic-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const enableTopic = async (topic) => {
        const response = await ADMIN_PANEL_PERFORMANCE_REPORTING_ENABLE_TOPIC.createRequest({ value: topic.topic })
            .send();

        setTopics(response);
        setConfirmationDialogData(null);
    }

    const disableTopic = async (topic) => {
        const response = await ADMIN_PANEL_PERFORMANCE_REPORTING_DISABLE_TOPIC.createRequest({ value: topic.topic })
            .send();

        setTopics(response);
        setConfirmationDialogData(null);
    }

    return (
        <div id="performance-reporting-topics">
            {getContent()}
        </div>
    )
}

export default PerformanceReportingTopics;