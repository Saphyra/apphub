function SyncEngine(cId, cnMethod, unMethod, sMethod, initialValues, idPref, aUpdate){
    logService.logToConsole("Creating new SyncEngine with containerId: " + cId + ", createNodeMethod: " + cnMethod + ", updateNodeMethod: " + unMethod + ", shortMethod: " + sMethod + ", idPrefix: " + idPref + ", allowUpdate: " + allowUpdate + ", and initialValues " + mapToString(initialValues));

    const nodeCache = {};
    const cache = initialValues ? setInitialValues(initialValues) : {};
    const containerId = cId || throwException("IllegalArgument", "containerId is not defined");
    const idPrefix = idPref || "";
    const allowUpdate = aUpdate || false;
    const createNodeMethod = cnMethod || throwException("IllegalArgument", "createNodeMethod is not defined");
    const updateNodeMethod = unMethod || null;
    const sortMethod = sMethod || function(a, b){return 0;};
    let order = getOrder();

    render();

    this.render = render;

    this.add = function(key, item){
        cache[key] = item;

        if(key in cache && allowUpdate){
            updateNodeMethod(nodeCache[key], item);

            const newOrder = getOrder();
            if(!arraysEqual(order, newOrder)){
                order = newOrder;
                render();
            }
        }else{
            nodeCache[key] = createNodeMethod(key);
            order = getOrder();
            render();
        }
    }

    this.remove = function(id){
        document.getElementById(containerId).removeChild(document.getElementById(createId(id)));

        delete cache[id];
        delete nodeCache[id];
    }

    function render(){
        const container = document.getElementById(containerId);
            container.innerHTML = "";

            new Stream(order)
                .map((key) => {return nodeCache[key]})
                .forEach(node => {container.appendChild(nodeCache[key])});
    }

    function getOrder(){
        return new Stream(Object.keys(cache))
            .sorted(function(a, b){return sortMethod(cache[a], cache[b])})
            .toList()
    }

    function setInitialValues(initialValues){
        return new MapStream(initialValues)
            .peek((key, value) => {nodeCache[key] = createNodeMethod(value)})
            .toMap();
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
    this.createNodeMethod = null;
    this.updateNodeMethod = null;
    this.shortMethod = null;
    this.idPrefix = "";

    this.withContainerId = function(cId){
        this.containerId = null;
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

    this.withCreateNodeMethod = function(method){
        this.createNodeMethod = method;
        return this;
    }

    this.withUpdateNodeMethod = function(method){
        this.updateNodeMethod = method;
        return this;
    }

    this.withShortMethod = function(method){
        this.shortMethod = method;
        return this;
    }

    this.withIdPrefix = function(prefix){
        this.idPrefix = prefix;
        return this;
    }

    this.build = function(){
        return new  SyncEngine(containerId, createNodeMethod, updateNodeMethod, shortMethod, initialValues, idPrefix, allowUpdate)
    }

}