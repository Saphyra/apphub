import Stream from "../../../../common/js/collection/Stream";
import Diagram from "./Diagram";
import MapStream from "../../../../common/js/collection/MapStream";
import Reports from "../Reports";
import { Order, OrderBy } from "../sort_selector/Order";

const Diagrams = ({ services, reports, duration, orderBy, order, localizationHandler }) => {
    const getDiagrams = () => {
        const grouped = new Stream(reports)
            .groupBy((item) => item.service)
            .toObject();

        return new MapStream(services)
            .filter((service, shouldDisplay) => shouldDisplay)
            .toListStream((service, shouldDisplay) => service)
            .map(service => new Reports(service, grouped[service]))
            .sorted((a, b) => Order[order](OrderBy[orderBy](a, b)))
            .map(r =>
                <Diagram
                    key={r.serviceName}
                    reports={r}
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