(function SqlGenerator(){
    window.sqlGenerator = new function(){
        this.generate = generate;
    }

    function generate(){
        const result = document.getElementById(ids.resultContainer);
            result.innerHTML = "";

        const messageType = document.getElementById(ids.messageTypeInput).value;
        const os = document.getElementById(ids.osInput).value;

        const enTitle = document.getElementById(ids.enTitleInput).value;
        const enDetail = document.getElementById(ids.enDetailInput).value;
        const deTitle = document.getElementById(ids.deTitleInput).value;
        const deDetail = document.getElementById(ids.deDetailInput).value;

        const sqls = [];

        //TODO generate SQLs

        new Stream(sqls)
            .map(createNode)
            .forEach(function(node){result.appendChild(node)});

        function createNode(sql){
            const node = document.createElement("DIV");
                node.innerHTML = sql;
            return node;
        }
    }
})();