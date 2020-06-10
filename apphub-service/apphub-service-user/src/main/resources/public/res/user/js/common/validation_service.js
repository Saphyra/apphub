(function ValidationService(){
    scriptLoader.loadScript("/res/common/js/validation_util.js");

    window.validationService = new function(){
        this.bulkValidation = function(validationCalls){
            return new Stream(validationCalls)
                .map(function(validationCall){return validationCall()})
                .toList();
        }

        this.validatePasswordFilled = function(invalidField, password){
            if(password.length < 1){
                return {
                    isValid: false,
                    process: createErrorProcess(invalidField, "password-empty")
                };
            }else{
                 return{
                     isValid: true,
                     process: createSuccessProcess(invalidField)
                 };
             }
        }

        this.validatePassword = function(invalidField, password){
            if(password.length < 6){
                return {
                    isValid: false,
                    process: createErrorProcess(invalidField, "password-too-short")
                };
            }else if(password.length > 30){
                return {
                    isValid: false,
                    process: createErrorProcess(invalidField,  "password-too-long")
                };
            }else{
                return{
                    isValid: true,
                    process: createSuccessProcess(invalidField)
                };
            }
        }

        this.validateConfirmPassword = function(invalidField, password, confirmPassword){
            if(password !== confirmPassword){
                return {
                    isValid: false,
                    process: createErrorProcess(invalidField, "incorrect-confirm-password")
                };
            }else{
                return{
                    isValid: true,
                    process: createSuccessProcess(invalidField)
                };
            }
        }

        this.validateEmail = function(invalidField, email){
            if(!isEmailValid(email)){
                return {
                    isValid: false,
                    process: createErrorProcess(invalidField, "email-invalid")
                };
            }else{
                return {
                    isValid: true,
                    process: createSuccessProcess(invalidField)
                };
            }
        }

        this.validateUsername = function(invalidField, username){
            if(username.length < 3){
                return {
                    isValid: false,
                    process: createErrorProcess(invalidField, "username-too-short")
                };
            }else if(username.length > 30){
                return {
                    isValid: false,
                    process: createErrorProcess(invalidField, "username-too-long")
                };
            }else{
                return {
                    isValid: true,
                    process: createSuccessProcess(invalidField)
                };
            }
        }
    }
})();