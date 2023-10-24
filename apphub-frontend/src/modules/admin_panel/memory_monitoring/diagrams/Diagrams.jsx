import React from "react";
import services from "./services.json";
import Stream from "../../../../common/js/collection/Stream";
import Diagram from "./Diagram";

const Diagrams = ({ reports, duration, localizationHandler }) => {
    const getDiagrams = () => {
        const grouped = new Stream(reports)
            .groupBy((item) => item.service)
            .toObject();

        return new Stream(services)
            .sorted((a, b) => a.localeCompare(b))
            .map(service =>
                <Diagram
                    key={service}
                    service={service}
                    reports={grouped[service]}
                    duration={duration}
                    localizationHandler={localizationHandler}
                />
            )
            .toList();

    }

    return (
        <div id="memory-monitoring-diagrams">
            {getDiagrams()}
        </div>
    );
}

export default Diagrams;