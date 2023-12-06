import React from "react";
import Stream from "../../../../common/js/collection/Stream";
import Diagram from "./Diagram";
import MapStream from "../../../../common/js/collection/MapStream";
import Utils from "../../../../common/js/Utils";

const Diagrams = ({ services, reports, duration, localizationHandler }) => {
    const getDiagrams = () => {
        const grouped = new Stream(reports)
            .groupBy((item) => item.service)
            .toObject();

        const c1 = new MapStream(services).toObject();

        return new MapStream(services)
            .filter((service, shouldDisplay) => shouldDisplay)
            .toListStream((service, shouldDisplay) => service)
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