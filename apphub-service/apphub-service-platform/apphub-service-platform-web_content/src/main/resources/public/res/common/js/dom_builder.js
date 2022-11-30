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

        this.getNode = function(){
            return node;
        }
    }
})();