(function EncodeController(){
    scriptLoader.loadScript("/res/common/js/base64.js");


    window.encodeController = new function(){
        this.encode = function(){displayResult(encodeInput);}
        this.decode = function(){displayResult(decodeInput);}
    }

    function displayResult(encoder){
        document.getElementById(ids.result).value = encoder(document.getElementById(ids.input).value);
    }

    function encodeInput(input){
        return Base64.encode(input);
    }

    function decodeInput(input){
        return Base64.decode(input);
    }
})();