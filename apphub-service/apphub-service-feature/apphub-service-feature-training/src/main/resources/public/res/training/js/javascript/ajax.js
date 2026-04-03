function syncRequest(){
    const request = new XMLHttpRequest();
        request.open("get", "/api/training/sample?param1=ertek1&param2=ertek2", false);
        request.send();
    if(request.status == 200){
        document.getElementById("sync_result").innerHTML = request.responseText;
    }else{
        alert("Hiba - " + request.status);
    }
}

function asyncRequest(){
    const request = new XMLHttpRequest();
    request.open("get", "/api/training/sample?param1=ertek1&param2=ertek2", true);
    request.onload = function(){
        if(request.status == 200){
            document.getElementById("async_result").innerHTML = request.responseText;
        }else{
            alert("Hiba - " + request.status);
        }
    }
    request.send();
}