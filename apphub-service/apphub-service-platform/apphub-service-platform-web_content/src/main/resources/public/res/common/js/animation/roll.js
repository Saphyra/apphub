(function Roll(){
    window.roll = new function(){
        scriptLoader.loadScript("/res/common/js/animation/domutil.js");
        this.rollInHorizontal = rollInHorizontal;
        this.rollOutHorizontal = rollOutHorizontal;

        this.rollInVertical = rollInVertical;
        this.rollOutVertical = rollOutVertical;
    }
    
    /*
    Adds a roll-in display effect for a given element.
    Arguments:
        - element: the element to display
        - container: the container to append the new element into.
        - time: the timeout of the roll-in effect.
    */
    function rollInHorizontal(element, container, time){
        try{
            const width = DOMUtil.getElementWidth(element, container);
                element.style.overflow = "hidden";
                element.style.width = 0;
            container.appendChild(element);
                
                const timeout = Math.round(time / width) * 3;
                
                let actualWidth = 0;
                return new Promise(function(resolve, reject){
                    const interval = setInterval(function(){
                        try{
                            actualWidth += 3;
                            element.style.width = actualWidth + "px";
                            if(actualWidth >= width){
                                clearInterval(interval);
                                resolve();
                            }
                        }catch(err){
                            const message = arguments.callee.name + " - " + err.name + ": " + err.message;
                            logService.log(message, "error");
                            reject();
                        }
                    }, timeout);
                });
        }catch(err){
            const message = arguments.callee.name + " - " + err.name + ": " + err.message;
            logService.log(message, "error");
        }
    }
    
    /*
    Adds a roll-out display effect for a given element.
    Arguments:
        - element: the element to display
        - time: the timeout of the roll-out effect.
    */
    function rollOutHorizontal(element, time){
        try{
            const timeout = Math.round(time / element.offsetWidth) * 3;
            
            let actualWidth = element.offsetWidth;
            return new Promise(function(resolve, reject){
                const interval = setInterval(function(){
                actualWidth -= 3;
                element.style.width = actualWidth + "px";
                if(actualWidth <= 0){
                    clearInterval(interval);
                    resolve();
                }
            }, timeout);
            });
        }catch(err){
            const message = arguments.callee.name + " - " + err.name + ": " + err.message;
            logService.log(message, "error");
        }
    }

    function rollInVertical(element, container, time){
        const height = DOMUtil.getElementHeight(element, container);
            element.style.overflow = "hidden";
        container.appendChild(element);

        const actualHeight = element.offsetHeight;
        $(element).height(0);

        return new Promise(function(resolve, reject){
            $(element).animate(
                {height: actualHeight},
                time,
                function(){resolve()}
             );
        });
    }

    function rollOutVertical(element, time){
        let actualHeight = element.offsetHeight;
        const timeout = Math.round(time / actualHeight) * 3;

        return new Promise(function(resolve, reject){
            const interval = setInterval(function(){
            actualHeight -= 3;
            element.style.height = actualHeight + "px";
            if(actualHeight <= 0){
                clearInterval(interval);
                resolve();
            }
        }, timeout);
        });
    }
})();