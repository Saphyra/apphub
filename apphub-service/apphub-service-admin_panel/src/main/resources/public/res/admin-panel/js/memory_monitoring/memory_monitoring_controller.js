(function MemoryMonitoringController(){
    const REPORT_CACHE_CAPACITY = 1800;
    const PIXEL_PER_REPORT = 2;
    const GRAPH_HEIGHT = 200;
    const GRAPH_BORDER = 5;

    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.memoryReports)
        .withGetKeyMethod(report => {return report.service})
        .withCreateNodeMethod(report => {return report.container})
        .withSortMethod((a, b)=>{return a.service.localeCompare(b.service)})
        .withIdPrefix("report-container")
        .build();

    wsConnection = new WebSocketConnection(Mapping.getEndpoint("WS_CONNECTION_ADMIN_PANEL_MONITORING"))
        .addHandler(new WebSocketEventHandler(
            (eventName)=>{return eventName == "admin-panel-monitoring-memory-status"},
            addMemoryReport
        ))
        .connect();

    const data = {};

    function addMemoryReport(memoryReport){
        const reportList = data[memoryReport.service] || createMemoryReport(memoryReport.service, memoryReport.availableMemoryBytes);

        reportList.add(memoryReport);
    }

    function createMemoryReport(service, availableMemoryBytes){
        console.log("Creating memoryReport for service " + service);
        const container = createContainer(service);
            syncEngine.add({service: service, container: container});

            const titleRow = document.createElement("TR");
                const titleCell = document.createElement("TH");
                    titleCell.colSpan = 2;
                    titleCell.innerText = service;
            titleRow.appendChild(titleCell);
        container.appendChild(titleRow);

            const contentRow = document.createElement("TR");
                const availableMemoryCell = document.createElement("TD");
                    availableMemoryCell.innerText = Localization.getAdditionalContent("available-memory", {memory: toMegaBytes(availableMemoryBytes)});
            contentRow.appendChild(availableMemoryCell);

                const svgCell = document.createElement("TD");
                    svgCell.rowSpan = 3;
                    const svg = createSvg(service);
                svgCell.appendChild(svg);
            contentRow.appendChild(svgCell);
        container.appendChild(contentRow);

            const allocatedRow = document.createElement("TR");
                const allocatedCell = document.createElement("TD");
            allocatedRow.appendChild(allocatedCell);
        container.appendChild(allocatedRow);

            const usedRow = document.createElement("TR");
                const usedCell = document.createElement("TD");
            usedRow.appendChild(usedCell);
        container.appendChild(usedRow);

        const result = new MemoryReports(service, svg, {allocated: allocatedCell, used: usedCell});
            data[service] = result;
        return result;

        function createContainer(service){
            const container = document.createElement("TABLE");
                container.id = "report-container-" + service;
                container.classList.add("report-container");
                container.classList.add("formatted-table");
            return container;
        }

        function createSvg(service){
            const svg = createSvgElement("svg");
                svg.id = "svg-container-" + service;
                svg.classList.add("svg-container");
            return svg;
        }
    }

    function MemoryReports(s, c, cells){
        const service = s || throwException("IllegalArgument", "service must not be null");
        const container = c || throwException("IllegalArgument", "container must not be null");
        const labelCells = cells || throwException("IllegalArgument", "labelCells must not be null");
        const reports = {};
        const nodes = {};

        this.add = function(memoryReport){
            reports[memoryReport.epochSeconds] = memoryReport;

            removeExpiredIfNecessary();

            labelCells.allocated.innerText = Localization.getAdditionalContent("allocated-memory", {memory: toMegaBytes(memoryReport.allocatedMemoryBytes)});
            labelCells.used.innerText = Localization.getAdditionalContent("used-memory", {memory: toMegaBytes(memoryReport.usedMemoryBytes)});

            render(service, container, reports, nodes, labelCells);
        }

        function removeExpiredIfNecessary(){
            const keys = Object.keys(reports);

            if(keys.length > REPORT_CACHE_CAPACITY){
                keys.sort((a, b)=>{return a - b});

                const itemToDelete = keys[0];

                delete reports[itemToDelete];
                delete nodes[itemToDelete];
            }
        }

        function render(service, container, reports, nodes){
            const numberOfItemsToDisplay = Number(document.getElementById(ids.itemsToDisplay).value);

            const keysToDisplay = new Stream(Object.keys(reports))
                .sorted((a, b)=>{return b - a})
                .limit(numberOfItemsToDisplay)
                .toList();

            const min = collectAllValues(keysToDisplay)
                .min();
            const max = collectAllValues(keysToDisplay)
                .max();
            container.setAttribute("viewBox", "0 0 " + (PIXEL_PER_REPORT * numberOfItemsToDisplay + GRAPH_BORDER * 2) + " " + (GRAPH_HEIGHT + GRAPH_BORDER * 2));

            const lines = getOrCreate(nodes, service, container);

            updateLine(lines.allocated, keysToDisplay, min, max, reports,  (report)=>{return report.allocatedMemoryBytes});
            updateLine(lines.used, keysToDisplay, min, max, reports,  (report)=>{return report.usedMemoryBytes});

            function updateLine(line, keysToDisplay, min, max, reports, valueExtractor){
                const points = [];
                for(let i = 0; i < keysToDisplay.length; i++){
                    const key = keysToDisplay[i];
                    const report = reports[Number(key)];

                    const x = GRAPH_BORDER + ((i + 1) * PIXEL_PER_REPORT);
                    const y = calculateHeight(0, max, valueExtractor(report));
                    points.push(x + "," + y);
                }

                const path = points.join(" ");
                line.setAttribute("points", path);
            }

            function collectAllValues(keysToDisplay){
                return new Stream(keysToDisplay)
                    .flatMap((key)=>{
                        const report = reports[key];
                        return [
                            report.availableMemoryBytes,
                            report.allocatedMemoryBytes,
                            report.usedMemoryBytes
                        ];
                    })
            }

            function getOrCreate(nodes, service, container){
                return nodes[service] || create(nodes, service, container);

                function create(nodes, service, container){
                    const lines = {
                        allocated: createNode(container, "allocated"),
                        used: createNode(container, "used"),
                    }

                    nodes[service] = lines;

                    return lines;

                    function createNode(container, type){
                        const svgElement = createSvgElement("polyline");
                            svgElement.classList.add("memory-report-item");
                            svgElement.classList.add(type);

                        container.appendChild(svgElement);
                        return svgElement;
                    }
                }
            }

            window.calculateHeight = calculateHeight;

            function calculateHeight(min, max, value){
                const diff = max - min;
                const modifiedValue = value - min;

                const result = GRAPH_HEIGHT - Math.round(modifiedValue / diff * GRAPH_HEIGHT) + GRAPH_BORDER;
                return result;
            }
        }
    }

    function toMegaBytes(bytes){
        return Math.round(bytes / 1024 / 1024);
    }
})();