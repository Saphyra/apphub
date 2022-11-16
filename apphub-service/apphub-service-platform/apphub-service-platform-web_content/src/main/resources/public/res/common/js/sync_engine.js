function SyncEngine(cId, keyMethod, cnMethod, unMethod, sMethod, initialValues, idPref, aUpdate, fMethod, ieMethod){
    console.log("Creating new SyncEngine with containerId: " + cId + "and idPrefix: " + idPref);

    let nodeCache = {};
    let cache = initialValues ? setInitialValues(initialValues) : {};
    const containerId = cId || throwException("IllegalArgument", "containerId is not defined");
    const idPrefix = idPref || "";
    const allowUpdate = aUpdate || false;
    const getKeyMethod = keyMethod || throwException("IllegalArgument", "getKeyMethod is not defined");
    const createNodeMethod = cnMethod || throwException("IllegalArgument", "createNodeMethod is not defined");
    const updateNodeMethod = unMethod || null;
    const sortMethod = sMethod || function(a, b){return 0;};
    const filterMethod = fMethod || throwException("IllegalArgument", "filterMethod is not defined")
    let order = getOrder();
    const ifEmptyMethod = ieMethod;

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
                if(!skipRender){
                    render();
                }
            }
        }else{
            nodeCache[key] = createNode(item);
            order = getOrder();
            if(!skipRender){
                render();
            }
        }
    }

    this.clear = function(){
        cache = {};
        nodeCache = {};
        order = [];
        render();
    }

    this.contains = function(item){
        return Object.keys(cache).indexOf(getKeyMethod(item)) > -1;
    }

    this.get = function(key){
        return cache[key];
    }

    this.keys = function(){
        return Object.keys(cache);
    }

    this.remove = function(key){
        if(Object.keys(cache).indexOf(key) < 0){
            return;
        }

        delete cache[key];
        delete nodeCache[key];

        render();
    }

    this.reload = function(){
        nodeCache = {};
        order = getOrder();

        new MapStream(cache)
            .forEach(function(key, item){
                nodeCache[key] = createNode(item);
            });
        render(order);
    }

    this.resort = function(){
        order = getOrder();
        render();
    }

    this.size = function(){
        return Object.keys(cache).length;
    }

    this.render = render;

    function render(order){
        const container = document.getElementById(containerId);
            container.innerHTML = "";

        if(Object.keys(cache).length == 0 && hasValue(ifEmptyMethod)){
            container.appendChild(ifEmptyMethod());
            return;
        }

        order = order || getOrder();

            new Stream(order)
                .filter((key) => {return filterMethod(cache[key])})
                .map((key) => {return nodeCache[key]})
                .forEach(node => {container.appendChild(node)});
    }

    this.renderNode = function(key){
        const oldNode = nodeCache[key];
        const item = cache[key];
        console.log(item);
        const newNode = createNode(item);

        oldNode.replaceWith(newNode);

        nodeCache[key] = newNode;
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
    this.filterMethod = function(a, b){
        return true;
    }
    this.ifEmptyMethod = null;

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

    this.withFilterMethod = function(method){
        this.filterMethod = method;
        return this;
    }

    this.withIfEmptyMethod = function(method){
        this.ifEmptyMethod = method;
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
            this.allowUpdate,
            this.filterMethod,
            this.ifEmptyMethod
        );
    }
}