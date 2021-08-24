(function MenuController(){
    const menuLocalization = new CustomLocalization("admin_panel", "index_menu");

    $(document).ready(function(){
        const request = new Request(Mapping.getEndpoint("ADMIN_PANEL_MENU"));
            request.convertResponse = function(response){
                return new Stream(JSON.parse(response.body))
                    .sorted(function(a, b){return menuLocalization.get(a.id).localeCompare(menuLocalization.get(b.id))})
                    .toList();
            }
            request.processValidResponse = function(menuItems){
                const menuContainer = document.getElementById("menu");

                new Stream(menuItems)
                    .map(createMenuNode)
                    .forEach(function(menuNode){menuContainer.appendChild(menuNode)});
            }
        dao.sendRequestAsync(request);
    });

    function createMenuNode(menuItem){
        const link = document.createElement("a");
            link.id = menuItem.id;
            link.classList.add("menu-item");
            link.innerHTML = menuLocalization.get(menuItem.id);
            link.href = menuItem.url;
        return link;
    }
})();