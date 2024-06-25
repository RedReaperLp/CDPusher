import {useEffect, useMemo, useRef, useState} from "react";
import Topbar from "./topbar/Topbar.jsx";
import Content from "./main/Content.jsx";
import Footer from "./footer/Footer.jsx";
import {fetchSongs} from "./Fetching.jsx";
import Swal from "sweetalert2";

let wsString;
if (window.location.hostname === "localhost") {
    wsString = "ws://";
} else {
    wsString = "wss://";
}

function discTopic(response) {
    console.log("Disc response: ", response)
    switch (response.type) {
        case "already_exists": {
            Swal.fire({
                title: "Disc already exists",
                html: "<a>The disc you are trying to add already exists in the database</a><br>" +
                    "Disc ID: " + response.conflict,
                icon: "error"
            });
            break;
        }

        case "failed": {
            Swal.fire({
                title: "Failed to add disc",
                html: "<a>There was an error adding the disc to the database</a><br>" +
                    "Error: " + response.error,
                icon: "error"
            })
            break;
        }

        case "pushed_to_db": {
            Swal.fire({
                title: "Disc added successfully",
                html: "<a>The disc has been added to the database</a><br>" +
                    "Disc ID: " + response.disc_id,
                icon: "success"
            });
            break;
        }

        case "still_mismatches": {
            Swal.fire({
                title: "There are still mismatches",
                text: "Please fix the songs and try again (" + response.songs.length + " songs)",
                icon: "error"
            });
            break;
        }

        case "still_indexing": {
            Swal.fire({
                title: "Still indexing",
                text: "Please wait for the disc to finish indexing",
                icon: "info"
            });
            break;
        }
    }
}

function clearSongs(storage) {
    storage.webSocket.send(JSON.stringify({
        request: "clear-songs"
    }));
}

function App() {
    const [songs, setSongs] = useState([]);
    const [render, setRender] = useState(false);
    const [discInfo, setDiscInfo] = useState({});
    const ws = useMemo(() => new WebSocket(wsString + window.location.hostname + "/api/ws/"), []); // Initialize WebSocket
    const username = "RerLp";
    const storageRef = useRef({
        songs: songs,
        setSongs: setSongs,
        webSocket: ws,
        discInfo: discInfo,
        setDiscInfo: setDiscInfo
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
                    case "disc-info": {
                        storageRef.current.setDiscInfo(response.info);
                        break;
                    }
                    case "song-response": {
                        const id = curSongs.findIndex(song => song.song_id === response.song.song_id);
                        if (response.song.song_cover_uri === null || !response.song.song_cover_uri) {
                            response.song.song_cover_uri = "/assets/images/svg/questionmark.svg";
                        }
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
                        storageRef.current.setDiscInfo({});
                        break;
                    case "song-update":
                        storageRef.current.setSongs(prevSongs => {
                            return prevSongs.map(song => {
                                if (song.song_id === response.song.song_id) {
                                    if (response.song.song_cover_uri === null || !response.song.song_cover_uri) {
                                        response.song.song_cover_uri = "/assets/images/svg/questionmark.svg";
                                    }
                                    console.log("Updated song: ", response.song);
                                    return response.song;
                                }
                                return song;
                            });
                        });
                        break;
                    case "push-database": {
                        if (response.status === "no-mismatch") {
                            Swal.fire({
                                title: "Songs successfully pushed to database",
                                icon: "success"
                            });
                            clearSongs(storage);
                        } else {
                            Swal.fire({
                                title: "There are still some mismatches",
                                text: "Please fix the songs and try again (" + response.songs.length + " songs)",
                            })
                        }
                        break;
                    }
                    default:
                        break;
                }

                switch (response.topic) {
                    case "disc": {
                        discTopic(response);
                        break;
                    }
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
            webSocket: storageRef.current.webSocket,
            discInfo: discInfo,
            setDiscInfo: setDiscInfo
        };
        setRender(!render);
    }, [songs]);

    useEffect(() => {
        fetchSongs(username).then(fetchedSongs => {
            setSongs(fetchedSongs.songs);
            setDiscInfo(fetchedSongs.disc);
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
