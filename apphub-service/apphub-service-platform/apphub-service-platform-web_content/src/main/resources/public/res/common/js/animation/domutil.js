(function DOMUtil(){
    window.DOMUtil = new function(){
        this.getElementWidth = getElementWidth;
    }
    
    /*
        Returns the absolute width of the element in pixels.
        Parameters:
            - element:
                - The DOM element to calculate the width of.
            - parent:
                - the parent object, to calculate with.
                - Optional. Default: body.
    */
    function getElementWidth(element, parent){
        try{
            parent = parent || document.body;
            
            const testElement = element.cloneNode(true);
                testElement.style.visibility = "hidden";
                testElement.style.position = "absolute";
            parent.appendChild(testElement);
            
            const width = testElement.offsetWidth;
            parent.removeChild(testElement);
            return width;
        }catch(err){
            const message = arguments.callee.name + " - " + err.name + ": " + err.message;
            logService.log(message, "error");
        }
    }
})();