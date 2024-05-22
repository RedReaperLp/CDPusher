import {useEffect, useMemo, useRef, useState} from "react";
import Topbar from "./topbar/Topbar.jsx";
import Content from "./main/Content.jsx";
import Footer from "./footer/Footer.jsx";
import {fetchSongs} from "./Fetching.jsx";
import Swal from "sweetalert2";

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
                const response = JSON.parse(event.data);
                const curSongs = storageRef.current.songs;
                switch (response.request) {
                    case "song-response": {
                        const id = curSongs.findIndex(song => song.trackID === response.song.trackID);
                        if (id !== -1) {
                            storageRef.current.setSongs(prevSongs => {
                                const updatedSongs = [...prevSongs];
                                updatedSongs[id] = response.song;
                                return updatedSongs;
                            });
                        } else {
                            storageRef.current.setSongs(prevSongs => [...prevSongs, response.song]);
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
                                if (song.trackID === response.song.trackID) {
                                    if (response.song.coverURI === null || !response.song.coverURI) {
                                        response.song.coverURI = "/assets/images/svg/questionmark.svg";
                                    }
                                    console.log("Updated song: ", response.song);
                                    return response.song;
                                }
                                return song;
                            });
                        });
                        break;
                    case "push-database": {
                        if (response.status === "no-missmatch") {
                            Swal.fire({
                                title: "Songs successfully pushed to database",
                                icon: "success"
                            });
                            storage.webSocket.send(JSON.stringify({
                                request: "clear-songs"
                            }));
                        } else {
                            Swal.fire({
                                title: "There are still some missmatches",
                                text: "Please fix the songs and try again (" + response.songs.length + " songs)",
                            })
                        }
                        break;
                    }
                    default:
                        break;
                }
            };

            storage.webSocket.onclose = async () => {
                console.log("WebSocket closed. Attempting to reconnect...");
                await new Promise(resolve => setTimeout(resolve, 5000));
                window.location.reload();
            };

            storage.webSocket.onerror = (error) => {
                console.error("WebSocket error: ", error);
                storage.webSocket.close();
            };
        }

        initializeWebSocket();
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
