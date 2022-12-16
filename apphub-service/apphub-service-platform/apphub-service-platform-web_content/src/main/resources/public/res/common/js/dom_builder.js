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

        this.attr = function(attribute, value){
            node[attribute] = value;
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
            const children = (typeof childFactory == "function") ? childrenFactory() : childrenFactory;

            if(!children instanceof Stream && !Array.isArray(children)){
                console.log(children);
                throwException("IllegalArgument", "Children is not a collection.");
            }

            new ((children instanceof Stream) ? children : new Stream(children))
                .forEach((node) => this.appendChild(node));

            return this;
        }

        this.appendTo = function(parent){
            parent.appendChild(node);
            return this;
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