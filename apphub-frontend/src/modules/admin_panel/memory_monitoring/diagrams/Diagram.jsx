import SvgDiagram from "./SvgDiagram";
import NotConnected from "./NotConnected";
import LocalDateTime from "../../../../common/js/date/LocalDateTime";
import { bytesToMegabytes } from "../../../../common/js/Utils";

const Diagram = ({ reports, duration, localizationHandler }) => {
    const getContent = () => {
        if (reports.lastReport == null || reports.lastReport.epochSeconds < LocalDateTime.now().getEpoch() / 1000 - 5) {
            return (
                <NotConnected localizationHandler={localizationHandler} />
            );
        }

        return (
            <div className="memory-monitoring-diagram-content">
                <div className="memory-monitoring-diagram-details">
                    <div className="memory-monitoring-available-memory">
                        <span>{localizationHandler.get("available")}: </span>
                        <span>{bytesToMegabytes(reports.availableMemory)} MB</span>
                    </div>

                    <div className="memory-monitoring-allocated-memory">
                        <span>{localizationHandler.get("allocated")}: </span>
                        <span>{bytesToMegabytes(reports.allocatedMemory)} MB</span>
                    </div>

                    <div className="memory-monitoring-used-memory">
                        <span>{localizationHandler.get("used")}: </span>
                        <span>{bytesToMegabytes(reports.latestUsedMemory)} MB</span>
                    </div>

                    <div className="memory-monitoring-max-used-memory">
                        <span>{localizationHandler.get("max-used")}: </span>
                        <span>{bytesToMegabytes(reports.maxUsedMemory)} MB</span>
                    </div>
                </div>

                <SvgDiagram
                    service={reports.serviceName}
                    availableBytes={reports.availableMemory}
                    duration={duration}
                    reports={reports.reports}
                />
            </div>
        );
    }

    return (
        <div
            id={"memory-monitoring-diagram-" + reports.serviceName}
            className="memory-monitoring-diagram"
        >
            <div className="memory-monitoring-diagram-title">{reports.serviceName}</div>

            {getContent()}
        </div>
    );
}

export default Diagram;