(function ChangeLanguageController(){
    const languageLocalization = localization.loadCustomLocalization("user", "language");

    pageLoader.addLoader(loadLanguages, "Load languages");
    pageLoader.addLoader(addEventListeners, "ChangeLanguage add event listeners");

    function loadLanguages(){
        const select = document.getElementById("ch-language-input");
            select.innerHTML = "";

        const request = new Request(Mapping.getEndpoint("ACCOUNT_GET_LANGUAGES"));
            request.convertResponse = function(response){
                return new Stream(JSON.parse(response.body))
                    .sorted(function(a, b){return languageLocalization.get(a.language).localeCompare(languageLocalization.get(b.language))})
                    .toList();
            }
            request.processValidResponse = function(languages){
                new Stream(languages)
                    .map(createOption)
                    .forEach(function(option){select.appendChild(option)});
            }
        dao.sendRequestAsync(request);

        function createOption(language){
            const option = document.createElement("option");
                option.innerHTML = languageLocalization.get(language.language);
                option.value = language.language;
                option.selected = language.actual;
            return option;
        }
    }

    function addEventListeners(){
        document.getElementById("change-language-button").onclick = saveLanguage;
    }

    function saveLanguage(){
        const language = document.getElementById("ch-language-input").value;

        const request = new Request(Mapping.getEndpoint("ACCOUNT_CHANGE_LANGUAGE"), {value: language});
            request.processValidResponse = function(){
                sessionStorage.successMessage = "language-changed";
                window.location.reload();
            }
        dao.sendRequestAsync(request);
    }
})();