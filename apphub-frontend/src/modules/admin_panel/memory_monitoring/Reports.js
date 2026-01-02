import Stream from "../../../common/js/collection/Stream";
import { hasValue } from "../../../common/js/Utils";

const Reports = class {
    constructor(serviceName, reports) {
        this.serviceName = serviceName;
        this.reports = reports;

        this.lastReport = new Stream(reports)
            .sorted((a, b) => b.epochSeconds - a.epochSeconds)
            .findFirst()
            .orElse(null);

        if (!hasValue(this.lastReport)) {
            return;
        }

        this.availableMemory = new Stream(reports)
            .map(report => report.availableMemoryBytes)
            .max()
            .orElse(0);

        this.allocatedMemory = this.lastReport.allocatedMemoryBytes;
        this.latestUsedMemory = this.lastReport.usedMemoryBytes;
        this.maxUsedMemory = new Stream(reports)
            .map(report => report.usedMemoryBytes)
            .max()
            .orElse(0);
    }
}

export default Reports;