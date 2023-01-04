(function PageController(){
    scriptLoader.loadScript("/res/user/js/account/change_email_controller.js");
    scriptLoader.loadScript("/res/user/js/account/change_language_controller.js");
    scriptLoader.loadScript("/res/user/js/account/change_password_controller.js");
    scriptLoader.loadScript("/res/user/js/account/change_username_controller.js");
    scriptLoader.loadScript("/res/user/js/account/delete_account_controller.js");

    $(document).ready(function(){
        localization.loadLocalization("user", "account");
    });
})();