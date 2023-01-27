function SyncEngine(cId, keyMethod, cnMethod, unMethod, sMethod, initialValues, idPref, fMethod, ieMethod, rcMethod){
    console.log("Creating new SyncEngine with containerId: " + cId + " and idPrefix: " + idPref);

    let nodeCache = {};
    let cache = (initialValues && initialValues.size > 0) ? addAll(initialValues, true) : {};
    const containerId = cId || throwException("IllegalArgument", "containerId is not defined");
    const idPrefix = idPref || "";
    const getKeyMethod = keyMethod || throwException("IllegalArgument", "getKeyMethod is not defined");
    const createNodeMethod = cnMethod || throwException("IllegalArgument", "createNodeMethod is not defined");
    const updateNodeMethod = unMethod || null;
    const sortMethod = sMethod || function(a, b){return 0;};
    const filterMethod = fMethod || throwException("IllegalArgument", "filterMethod is not defined")
    let order = getOrder();
    const ifEmptyMethod = ieMethod;
    const renderCallbackMethod = hasValue(rcMethod) ? rcMethod : function(){};

    if(Object.keys(cache).length > 0){
        render();
    }

    this.render = render;
    this.addAll = addAll;
    this.add = add;
    this.clear = clear;
    this.contains = contains;
    this.get = get;
    this.getCache = getCache;
    this.keys = keys;
    this.remove = remove;
    this.reload = reload;
    this.resort = resort;
    this.size = size;
    this.render = render;
    this.renderNode = renderNode;
    this.applyToAll = applyToAll;
    this.indexOf = indexOf;

    function indexOf(item){
        return Object.keys(cache)
            .indexOf(getKeyMethod(item));
    }

    function getCache(){
        return cache;
    }

    function applyToAll(method){
        new MapStream(cache)
            .forEach((key, value) => method(value));

        return this;
    }

    function contains(item){
        return Object.keys(cache).indexOf(getKeyMethod(item)) > -1;
    }

    function clear(skipRender){
        cache = {};
        nodeCache = {};
        order = [];

        if(!skipRender){
            render();
        }

        return this;
    }

    function add(item, skipRender){
        const key = getKeyMethod(item);
        cache[key] = item;

        if(key in nodeCache && updateNodeMethod != null){
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

        return this;
    }

    function addAll(items, skipRender){
        const addFunction = this.add;

        new Stream(items)
            .forEach(function(item){addFunction(item, true)});

        if(!skipRender){
            render();
        }

        return this;
    }

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

        applyToAll(renderCallbackMethod);

        return this;
    }

    function renderNode(key){
        const item = cache[key];
        if(!hasValue(item)){
            console.log("Cache content", cache);
            throwException("IllegalArgument", "There is no item in cache with key " + key);
        }
        const oldNode = nodeCache[key];
        const newNode = createNode(item);

        oldNode.replaceWith(newNode);

        nodeCache[key] = newNode;

        return this;
    }

    function get(key){
        return cache[key];
    }

    function reload(){
        nodeCache = {};
        order = getOrder();

        new MapStream(cache)
            .forEach(function(key, item){
                nodeCache[key] = createNode(item);
            });
        render(order);

        return this;
    }

    function keys(){
        return Object.keys(cache);
    }

    function getOrder(){
        return new Stream(Object.keys(cache))
            .sorted(function(a, b){return sortMethod(cache[a], cache[b])})
            .toList()
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

    function size(){
        return Object.keys(cache).length;
    }

    function resort(){
        order = getOrder();
        render();

        return this;
    }

    function remove(key){
        if(Object.keys(cache).indexOf(key) < 0){
            return;
        }

        delete cache[key];
        delete nodeCache[key];

        render();

        return this;
    }
}

function SyncEngineBuilder(){
    this.containerId = null;
    this.initialValues = [];
    this.getKeyMethod = null;
    this.createNodeMethod = null;
    this.updateNodeMethod = null;
    this.sortMethod = null;
    this.idPrefix = "";
    this.filterMethod = function(a, b){
        return true;
    }
    this.ifEmptyMethod = null;
    this.renderCallbackMethod = null;

    this.withContainerId = function(cId){
        this.containerId = cId;
        return this;
    }

    this.withInitialValues = function(init){
        this.initialValues = init || [];
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

    this.withRenderCallbackMethod = function(method){
        this.renderCallbackMethod = method;
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
            this.filterMethod,
            this.ifEmptyMethod,
            this.renderCallbackMethod
        );
    }
}