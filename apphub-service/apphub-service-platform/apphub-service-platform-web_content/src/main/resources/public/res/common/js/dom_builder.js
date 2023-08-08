(function(){
    window.domBuilder = new function(){
        this.create = function(input, content){
            const result = new DomBuilder(input)

            if(content){
                if(typeof content != "object"){
                    result.innerText(content);
                }else{
                    result.appendChild(content);
                }
            }

            return result;
        }
        this.titleBuilder = function(){
            return new TitleBuilder();
        }
    }

    function DomBuilder(input){
        const node = (typeof input == "string") ? document.createElement(input) : input;

        this.id = function(id){
            node.id = "id";

            return this;
        }
        this.addClass = function(clazz){
            node.classList.add(clazz);
            return this;
        }

        this.addClassIf = function(predicate, clazz){
            if(predicate(node)){
                node.classList.add(clazz);
            }
            return this;
        }

        this.attr = function(attribute, value){
            node[attribute] = value;
            return this;
        }

        this.style = function(attribute, value){
            node.style[attribute] = value;
            return this;
        }

        this.innerText = function(text){
            node.innerText = text;
            return this;
        }

        this.title = function(title){
            node.title = title;
            return this;
        }

        this.onclick = function(callback){
            node.onclick = callback;
            return this;
        }

        this.onchange = function(callbackSupplier){
            node.onchange = callbackSupplier(node);
            return this;
        }

        this.getNode = function(){
            return node;
        }

        this.appendChild = function(childFactory){
            let child = (typeof childFactory == "function") ? childFactory() : childFactory;
            if(child){
                if(child instanceof DomBuilder){
                    child = child.getNode();
                }

                node.appendChild(child);
            }
            return this;
        }

        this.appendChildren = function(childrenFactory){
            let children = (typeof childrenFactory == "function") ? childrenFactory() : childrenFactory;

            if(!(children instanceof Stream) && !Array.isArray(children)){

                if(!hasValue(children)){
                    children = [];
                }else{
                    children = [children];
                }
            }

            new ((children instanceof Stream) ? children : new Stream(children))
                .forEach((node) => this.appendChild(node));

            return this;
        }

        this.appendTo = function(parent){
            parent.appendChild(node);
            return this;
        }

        this.type = function(type){
            node.type = type;
            return this;
        }

        this.value = function(value){
            node.value = value;
            return this;
        }

        this.getValue = function(){
            return node.value;
        }
    }

    function TitleBuilder(){
        let title = "";

        this.append = function(text){
            title += text;
            return this;
        }

        this.appendLine = function(line, indentation){
            indentation = indentation || 0;

            if(title.length > 0){
                this.newLine();
            }

            for(let i = 0; i < indentation; i++){
                this.append(" ");
            }

            return this.append(line);
        }

        this.newLine = function(){
            title += LINE_SEPARATOR;
            return this;
        }

        this.build = function(){
            return title;
        }
    }
})();