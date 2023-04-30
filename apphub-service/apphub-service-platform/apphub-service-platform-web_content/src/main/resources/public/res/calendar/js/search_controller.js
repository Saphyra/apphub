scriptLoader.loadScript("/res/common/js/sync_engine.js");
scriptLoader.loadScript("/res/common/js/animation/roll.js");

(function SearchController(){
    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.searchResult)
        .withGetKeyMethod((event) => {return event.eventId})
        .withCreateNodeMethod(createNode)
        .withSortMethod((a, b) => {return a.time.localeCompare(b.time)})
        .withIdPrefix("search-result")
        .build();

    window.searchController = new function(){
        this.searchByFooter = function(){
            search(document.getElementById(ids.searchInput).value);
        };
        this.searchBySearchPage = function(){
            search(document.getElementById(ids.searchResultPageSearchInput).value);
        }
    }

    function search(query){
        if(query.length < 3){
            notificationService.showError(localization.getAdditionalContent("search-text-too-short"));
            return;
        }

        document.getElementById(ids.searchResultPageSearchInput).value = query;

        const request = new Request(Mapping.getEndpoint("CALENDAR_SEARCH"), {value: query});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(result){
                if(result.length == 0){
                    notificationService.showError(localization.getAdditionalContent("search-result-empty"));
                    return;
                }

                syncEngine.clear();
                syncEngine.addAll(result);
                switchTab("main-page", ids.searchResultPage);
            }
        dao.sendRequestAsync(request);
    }

    function createNode(searchResult){
        const node = document.createElement("DIV");
            node.classList.add("search-result");

            const nodeHeader = document.createElement("DIV");
                nodeHeader.classList.add("search-result-header");

                const nodeTitle = document.createElement("DIV");
                    nodeTitle.classList.add("search-result-title");
                    nodeTitle.title = searchResult.content;
                    nodeTitle.innerText = searchResult.title;
            nodeHeader.appendChild(nodeTitle);

                const toggleButton = document.createElement("BUTTON");
                    toggleButton.classList.add("search-result-toggle-button");
                    toggleButton.innerText = "V";
            nodeHeader.appendChild(toggleButton);
        node.appendChild(nodeHeader);

            const occurrenceContainer = document.createElement("DIV");
                occurrenceContainer.classList.add("search-result-occurrences");

                new Stream(searchResult.occurrences)
                    .sorted(occurrenceComparator)
                    .map((occurrence) => {return createOccurrence(searchResult, occurrence)})
                    .forEach((occurrenceNode) => occurrenceContainer.appendChild(occurrenceNode));

        node.appendChild(occurrenceContainer);

            const switchFunction = new Switch(
                () => {
                    toggleButton.innerText = "^";
                    roll.rollInVertical(occurrenceContainer, node);
                },
                () => {
                    toggleButton.innerText = "V";
                    roll.rollOutVertical(occurrenceContainer);
                }
            )

            toggleButton.onclick = () => switchFunction.apply();
        return node;

        function createOccurrence(searchResult, occurrence){
            const node = document.createElement("DIV");
                node.classList.add("search-result-occurrence");
                node.classList.add("button");
                node.classList.add(occurrence.status.toLowerCase());
                node.innerText = occurrence.date + parseTime(occurrence.time) + (occurrence.note == null ? "" : " - " + occurrence.note);

                node.onclick = function(e){
                    const event = {
                        date: occurrence.date,
                        time: occurrence.time,
                        occurrenceId: occurrence.occurrenceId,
                        eventId: searchResult.eventId,
                        status: occurrence.status,
                        title: searchResult.title,
                        content: searchResult.content,
                        note: occurrence.note
                    }

                    viewEventController.viewEvent(event);
                }
            return node;
        }
    }
})();