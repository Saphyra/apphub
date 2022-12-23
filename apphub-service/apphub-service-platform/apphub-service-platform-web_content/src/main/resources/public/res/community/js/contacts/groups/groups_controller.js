scriptLoader.loadScript("/res/common/js/sync_engine.js");
scriptLoader.loadScript("/res/community/js/contacts/groups/create_group_controller.js");
scriptLoader.loadScript("/res/community/js/contacts/groups/group_details_controller.js");
scriptLoader.loadScript("/res/community/js/contacts/groups/add_group_member_controller.js");

(function GroupsController(){
    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.contactsGroupsList)
        .withGetKeyMethod((group) => {return group.groupId})
        .withCreateNodeMethod(createGroupNode)
        .withSortMethod((a, b) => {return a.name.localeCompare(b.name)})
        .withIdPrefix("group")
        .withFilterMethod((group)=> {
            const filterValue = document.getElementById(ids.contactsGroupsSearchInput).value;
            return group.name.toLowerCase().indexOf(filterValue.toLowerCase()) > -1;
        })
        .build();

    pageLoader.addLoader(addEventListener, "Add eventListener to Groups search input");

    window.groupsController = new function(){
        this.load = load;
        this.addGroup = function(group){
            syncEngine.add(group);
        }
        this.removeGroup = function(groupId){
            syncEngine.remove(groupId);
        }
    }

    function load(){
        const request = new Request(Mapping.getEndpoint("COMMUNITY_GET_GROUPS"));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(groups){
                syncEngine.clear();
                syncEngine.addAll(groups);
            }
        dao.sendRequestAsync(request);
    }

    function createGroupNode(group){
        const node = document.createElement("DIV");
            node.classList.add("list-item");
            node.classList.add("button");

            node.innerText = group.name;

            node.onclick = function(){
                groupDetailsController.openGroup(group);
            }
        return node;
    }

    function addEventListener(){
        document.getElementById(ids.contactsGroupsSearchInput).onkeyup = function(){
            syncEngine.render();
        }
    }
})();