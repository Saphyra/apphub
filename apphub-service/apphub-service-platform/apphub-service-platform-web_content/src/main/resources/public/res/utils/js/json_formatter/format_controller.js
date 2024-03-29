(function FormatController(){
    window.formatController = new function(){
        this.format = format;
    }

    function format(){
        const input = document.getElementById(ids.input).value;

        const resultContainer = document.getElementById(ids.resultContainer);
            result.innerHTML = "";
        try{
            const obj = JSON.parse(input);
            const formatted = JSON.stringify(obj, null, 4);
            const highlighted = syntaxHighlight(formatted)
                .replaceAll("\\n", "<BR>")
                .replaceAll("\\r", "")
                .replaceAll("\\t", "    ");
            result.innerHTML = highlighted;
        }catch(e){
            notificationService.showError(localization.getAdditionalContent("json-processing-failed"));
        }
    }

    function syntaxHighlight(json) {
        json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
            var cls = 'number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'key';
                } else {
                    cls = 'string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'boolean';
            } else if (/null/.test(match)) {
                cls = 'null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        });
    }
})();