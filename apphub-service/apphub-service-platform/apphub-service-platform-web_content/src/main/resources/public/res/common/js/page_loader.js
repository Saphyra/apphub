(function initPageLoader(){
     const loaders = [];

     window.pageLoader = new function(){
         this.addLoader = function(loader, description){
             console.log("Adding loader " + description);
             if(!hasValue(description)){
                 throwException("IllegalArgument", "Description must not be null or undefined.");
             }

             if(!isFunction(loader)){
                 throwException("IllegalArgument", "Loader is not a function.");
             }

             loaders.push({load: loader, description: description});
         }
         this.runLoaders = function(){
            return new Promise((res, rej) => {
                let counter = 0;

                     const promises = new Stream(loaders)
                         .forEach(
                             function(loader){
                                 new Promise((resolve, reject) => {
                                     setTimeout(
                                         function(){
                                             console.log("Calling loader: " + loader.description);
                                             try{
                                                 loader.load();
                                             }catch(e){
                                                 reject();
                                                 throw e;
                                             }
                                             resolve();
                                         },
                                         0
                                     )
                                 })
                                 .then(()=>counter++, ()=>counter++);
                             }
                         );

                     const interval = setInterval(
                         function(){
                             console.log("Number of loaders: " + loaders.length + ", completed: " + counter);
                             if(counter == loaders.length){
                                 clearInterval(interval);
                                 res();
                             }
                         },
                         100
                     )
                 }
            );
        }
     }
 })();