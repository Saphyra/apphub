function SyncEngine(cId, keyMethod, cnMethod, unMethod, sMethod, initialValues, idPref, aUpdate){
    logService.logToConsole("Creating new SyncEngine with containerId: " + cId + ", createNodeMethod: " + cnMethod + ", updateNodeMethod: " + unMethod + ", shortMethod: " + sMethod + ", idPrefix: " + idPref + ", allowUpdate: " + aUpdate + ", and initialValues " + mapToString(initialValues));

    let nodeCache = {};
    let cache = initialValues ? setInitialValues(initialValues) : {};
    const containerId = cId || throwException("IllegalArgument", "containerId is not defined");
    const idPrefix = idPref || "";
    const allowUpdate = aUpdate || false;
    const getKeyMethod = keyMethod || throwException("IllegalArgument", "getKeyMethod is not defined");
    const createNodeMethod = cnMethod || throwException("IllegalArgument", "createNodeMethod is not defined");
    const updateNodeMethod = unMethod || null;
    const sortMethod = sMethod || function(a, b){return 0;};
    let order = getOrder();

    if(Object.keys(cache).length > 0){
        render();
    }

    this.render = render;

    this.addAll = function(items){
        const addFunction = this.add;

        new Stream(items)
            .forEach(function(item){addFunction(item, true)});

        render();
    }

    this.add = function(item, skipRender){
        const key = getKeyMethod(item);
        cache[key] = item;

        if(key in cache && allowUpdate){
            updateNodeMethod(nodeCache[key], item);

            const newOrder = getOrder();
            if(!arraysEqual(order, newOrder)){
                order = newOrder;
                render();
            }
        }else{
            nodeCache[key] = createNode(item);
            order = getOrder();
            render();
        }
    }

    this.clear = function(){
        console.log("Clear...");

        cache = {};
        nodeCache = {};
        order = [];
        render();
    }

    this.get = function(key){
        return cache[key];
    }

    this.remove = function(key){
        document.getElementById(containerId).removeChild(document.getElementById(createId(key)));

        delete cache[key];
        delete nodeCache[key];
    }

    this.reload = function(){
        console.log("Reload");

        nodeCache = {};
        order = getOrder();

        new MapStream(cache)
            .forEach(function(key, item){
                nodeCache[key] = createNode(item);
            });
        render(order);
    }

    this.resort = function(){
        console.log("Resort");

        order = getOrder();
        render();
    }

    this.size = function(){
        return Object.keys(cache).length;
    }

    function render(order){
        console.log("Render", order);

        order = order || getOrder();
        const container = document.getElementById(containerId);
            container.innerHTML = "";

            new Stream(order)
                .map((key) => {return nodeCache[key]})
                .forEach(node => {container.appendChild(node)});
    }

    function getOrder(){
        return new Stream(Object.keys(cache))
            .sorted(function(a, b){return sortMethod(cache[a], cache[b])})
            .toList()
    }

    function setInitialValues(initialValues){
        return new MapStream(initialValues)
            .peek((key, item) => {nodeCache[key] = createNode(item)})
            .toMap();
    }

    function createNode(item){
        const node = createNodeMethod(item);
            node.id = createId(getKeyMethod(item));
        return node;
    }

    function createId(id){
        if(idPrefix.length > 0){
            return idPrefix + "-" + id;
        }
        return id;
    }
}

function SyncEngineBuilder(){
    this.containerId = null;
    this.initialValues = {};
    this.allowUpdate = false;
    this.getKeyMethod = null;
    this.createNodeMethod = null;
    this.updateNodeMethod = null;
    this.sortMethod = null;
    this.idPrefix = "";

    this.withContainerId = function(cId){
        this.containerId = cId;
        return this;
    }

    this.withInitialValues = function(init){
        this.initialValues = initialValues;
        return this;
    }

    this.withAllowUpdate = function(value){
        allowUpdate = value;
        return this;
    }

    this.withGetKeyMethod = function(method){
        this.getKeyMethod = method;
        return this;
    }

    this.withCreateNodeMethod = function(method){
        this.createNodeMethod = method;
        return this;
    }

    this.withUpdateNodeMethod = function(method){
        this.updateNodeMethod = method;
        return this;
    }

    this.withSortMethod = function(method){
        this.sortMethod = method;
        return this;
    }

    this.withIdPrefix = function(prefix){
        this.idPrefix = prefix;
        return this;
    }

    this.build = function(){
        return new  SyncEngine(
            this.containerId,
            this.getKeyMethod,
            this.createNodeMethod,
            this.updateNodeMethod,
            this.sortMethod,
            this.initialValues,
            this.idPrefix,
            this.allowUpdate
        );
    }
}