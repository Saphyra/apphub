(function(){
    window.domBuilder = new function(){
        this.create = function(input){
            return new DomBuilder(input);
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

        this.innerText = function(text){
            node.innerText = text;
            return this;
        }

        this.onclick = function(callback){
            node.onclick = callback;
            return this;
        }

        this.getNode = function(){
            return node;
        }

        this.appendTo = function(parent){
            parent.appendChild(node);
            return this;
        }
    }
})();