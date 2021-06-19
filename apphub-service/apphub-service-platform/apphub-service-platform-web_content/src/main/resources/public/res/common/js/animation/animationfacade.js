(function AnimationFacade(){
    window.animationFacade = new function(){
        scriptLoader.loadScript("/res/common/js/animation/roll.js");
        
        this.rollInHorizontal = roll.rollInHorizontal;
        this.rollOutHorizontal = roll.rollOutHorizontal;
        this.rollInVertical = roll.rollInVertical;
    }
})();