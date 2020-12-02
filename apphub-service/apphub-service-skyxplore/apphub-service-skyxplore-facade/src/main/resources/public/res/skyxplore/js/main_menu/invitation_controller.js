(function InvitationController(){
    const socket = new WebSocket("ws://localhost:9001" + Mapping.getEndpoint("CONNECTION_SKYXPLORE_MAIN_MENU").getUrl());

    socket.onmessage = displayInvitation;

    function displayInvitation(event){
        notificationService.showSuccess(event.data);
    }
})();