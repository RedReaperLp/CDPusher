import {useEffect, useMemo, useRef, useState} from "react";
import Topbar from "./topbar/Topbar.jsx";
import Content from "./main/Content.jsx";
import Footer from "./footer/Footer.jsx";
import {fetchSongs} from "./Fetching.jsx";

function App() {
    const [songs, setSongs] = useState([]);
    const [render, setRender] = useState(false);
    const username = "RerLp";
    const ws = useMemo(() => new WebSocket("wss://redreaperlp.de/api/ws/"), []); // Initialize WebSocket
    const storageRef = useRef({
        songs: songs,
        setSongs: setSongs,
        webSocket: ws
    });

    useEffect(() => {
        const storage = storageRef.current;

        function initializeWebSocket() {
            storage.webSocket.onopen = () => {
                storage.webSocket.send(JSON.stringify({
                    request: "login",
                    username: username
                }));
                ping();
            };

            storage.webSocket.onmessage = (event) => {
                const object = JSON.parse(event.data);
                const curSongs = storageRef.current.songs;
                switch (object.request) {
                    case "song-response": {
                        const id = curSongs.findIndex(song => song.trackID === object.song.trackID);
                        if (id !== -1) {
                            storageRef.current.setSongs(prevSongs => {
                                const updatedSongs = [...prevSongs];
                                updatedSongs[id] = object.song;
                                return updatedSongs;
                            });
                        } else {
                            storageRef.current.setSongs(prevSongs => [...prevSongs, object.song]);
                        }
                        const el = document.querySelector(".content");

                        if (el && el.childElementCount % 5 === 0) el.lastChild.scrollIntoView({
                            behavior: "smooth",
                            block: "end",
                            inline: "nearest"
                        });
                        break;
                    }
                    case "clear-songs":
                        storageRef.current.setSongs([]);
                        break;
                    case "song-update":
                        storageRef.current.setSongs(prevSongs => {
                            return prevSongs.map(song => {
                                if (song.trackID === object.song.trackID) {
                                    console.log("Updated song: ", object.song);
                                    return object.song;
                                }
                                return song;
                            });
                        });
                        break;
                    default:
                        break;
                }
            };

            storage.webSocket.onclose = () => {
                console.log("WebSocket closed. Attempting to reconnect...");
                reconnectWebSocket();
            };

            storage.webSocket.onerror = (error) => {
                console.error("WebSocket error: ", error);
                storage.webSocket.close();
            };
        }

        function reconnectWebSocket() {
            storage.webSocket = new WebSocket("wss://redreaperlp.de/api/ws/");
            initializeWebSocket();
        }

        initializeWebSocket();

        return () => {
            if (storage.webSocket) {
                storage.webSocket.close();
            }
        };
    }, [ws]);

    useEffect(() => {
        storageRef.current = {
            setSongs: setSongs,
            songs: songs,
            webSocket: storageRef.current.webSocket
        };
        setRender(!render);
    }, [songs]);

    useEffect(() => {
        fetchSongs(username).then(fetchedSongs => {
            setSongs(fetchedSongs);
        });
    }, []);

    function ping() {
        setInterval(() => {
            if (storageRef.current.webSocket.readyState === WebSocket.OPEN) {
                storageRef.current.webSocket.send(JSON.stringify({request: "ping"}));
            }
        }, 5000);
    }

    return (
        <div className={"container"}>
            <Topbar storage={storageRef.current}/>
            <Content storage={storageRef.current}/>
            <Footer storage={storageRef.current}/>
        </div>
    );
}

export default App;
