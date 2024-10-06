import React, { useEffect, useState } from "react";
import PlanetHeader from "./header/PlanetHeader";
import "./planet.css";
import PlanetOverview from "./overview/PlanetOverview";
import PlanetSurface from "./surface/PlanetSurface";
import PlanetQueue from "./queue/PlanetQueue";
import useWebSocket from "react-use-websocket";
import WebSocketEventName from "../../../../../common/hook/ws/WebSocketEventName";
import WebSocketEndpoint from "../../../../../common/hook/ws/WebSocketEndpoint";
import { hasValue } from "../../../../../common/js/Utils";
import { SKYXPLORE_PLANET_GET_OVERVIEW } from "../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";

const Planet = ({ footer, planetId, closePage, openPage, setConfirmationDialogData }) => {
    //Planet data
    const [planetName, setPlanetName] = useState("");
    const [storage, setStorage] = useState(null);
    const [population, setPopulation] = useState({});
    const [buildings, setBuildings] = useState({});
    const [surfaces, setSurfaces] = useState([]);
    const [priorities, setPriorities] = useState({});
    const [queue, setQueue] = useState([]);

    useEffect(() => loadPlanet(), []);
    //End planet data

    //===WebSocket
    const webSocketUrl = "ws://" + window.location.host + WebSocketEndpoint.SKYXPLORE_GAME_PLANET;
    const [lastEvent, setLastEvent] = useState(null);
    const { sendMessage, lastMessage } = useWebSocket(
        webSocketUrl,
        {
            share: true,
            shouldReconnect: () => true,
        }
    );
    useEffect(() => handleMessage(), [lastMessage]);
    useEffect(() => sendPlanetId(), [sendMessage]);

    const sendPlanetId = () => {
        if (!hasValue(sendMessage)) {
            return;
        }

        const event = {
            eventName: WebSocketEventName.SKYXPLORE_GAME_PLANET_OPENED,
            payload: planetId
        };
        sendMessage(JSON.stringify(event));
    }

    const handleMessage = () => {
        if (lastMessage === null) {
            return;
        }

        const message = JSON.parse(lastMessage.data);

        if (message.eventName === WebSocketEventName.PING) {
            sendMessage(lastMessage.data);
        }

        setLastEvent(message);
    }
    //===End WebSocket

    //Data handling
    const loadPlanet = () => {
        const fetch = async () => {
            const response = await SKYXPLORE_PLANET_GET_OVERVIEW.createRequest(null, { planetId: planetId })
                .send();

            setPlanetName(response.planetName);
            setStorage(response.storage);
            setPopulation(response.population);
            setBuildings(response.buildings);
            setSurfaces(response.surfaces);
            setPriorities(response.priorities);
            setQueue(response.queue);
        }
        fetch();
    }

    useEffect(() => handleEvent(), [lastEvent]);

    const handleEvent = () => {
        if (!hasValue(lastEvent)) {
            return;
        }

        switch (lastEvent.eventName) {
            case WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED:
                const payload = lastEvent.payload;
                if (hasValue(payload.queue)) {
                    setQueue(payload.queue);
                }
                if (hasValue(payload.storage)) {
                    setStorage(payload.storage);
                }
                if (hasValue(payload.surfaces)) {
                    setSurfaces(payload.surfaces);
                }
                if (hasValue(payload.population)) {
                    setPopulation(payload.population);
                }
                if (hasValue(payload.buildings)) {
                    setBuildings(payload.buildings);
                }
                break;
        }
    }
    //Data handling

    return (
        <div>
            <PlanetHeader
                planetId={planetId}
                planetName={planetName}
                setPlanetName={setPlanetName}
                closePage={closePage}
            />

            <main id="skyxplore-game-planet">
                <PlanetOverview
                    storage={storage}
                    population={population}
                    buildings={buildings}
                    planetSize={surfaces.length}
                    priorities={priorities}
                    setPriorities={setPriorities}
                    planetId={planetId}
                    openPage={openPage}
                />

                <PlanetSurface
                    surfaces={surfaces}
                    setConfirmationDialogData={setConfirmationDialogData}
                    planetId={planetId}
                    openPage={openPage}
                />

                <PlanetQueue
                    queue={queue}
                    planetId={planetId}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            </main>

            {footer}
        </div>
    );
}

export default Planet;