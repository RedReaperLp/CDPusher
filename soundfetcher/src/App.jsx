import {useEffect, useMemo, useRef, useState} from "react";
import Topbar from "./topbar/Topbar.jsx";
import Content from "./main/Content.jsx";
import Footer from "./footer/Footer.jsx";
import {fetchSongs} from "./Fetching.jsx";
import Swal from "sweetalert2";
import {findTopic, Topic} from "./Topic.js";

let wsString;
if (window.location.hostname === "localhost") {
    wsString = "ws://";
} else {
    wsString = "wss://";
}

function discTopic(response, storageRef) {
    switch (response.type) {
        case Topic.DISC.ALREADY_EXISTS: {
            Swal.fire({
                title: "Disc already exists",
                html: "<a>The disc you are trying to add already exists in the database</a><br>" +
                    "Disc ID: " + response.conflict,
                icon: "error"
            });
            break;
        }

        case Topic.DISC.FAILED: {
            Swal.fire({
                title: "Failed to add disc",
                html: "<a>There was an error adding the disc to the database</a><br>" +
                    "Error: " + response.error,
                icon: "error"
            })
            break;
        }

        case Topic.DISC.PUSHED_TO_DB: {
            Swal.fire({
                title: "Disc added successfully",
                html: "<a>The disc has been added to the database</a><br>" +
                    "Disc ID: " + response.disc_id,
                icon: "success"
            });
            break;
        }

        case Topic.DISC.STILL_MISMATCHES: {
            Swal.fire({
                title: "There are still mismatches",
                text: "Please fix the songs and try again (" + response.songs.length + " songs)",
                icon: "error"
            });
            break;
        }

        case Topic.DISC.STILL_INDEXING: {
            Swal.fire({
                title: "Still indexing",
                text: "Please wait for the disc to finish indexing",
                icon: "info"
            });
            break;
        }

        case Topic.DISC.NOT_FOUND: {
            Swal.fire({
                title: "Disc not found",
                html: "<a>The disc you are trying to add was not found</a><br>" +
                    "Are you sure you entered the correct ean?",
                icon: "error"
            });
            break;
        }

        case Topic.DISC.CLEAR: {
            storageRef.current.setSongs([]);
            storageRef.current.setDiscInfo({});

            Swal.fire({
                title: "Songs cleared",
                icon: "success",
                timer: 1000
            });
            break
        }

        case Topic.DISC.SUBMIT_DISC_INFO : {
            storageRef.current.setDiscInfo(response.info);
        }
    }
}

function searchTopic(response, storageRef) {
    switch (response.type) {
        case Topic.SEARCH.START: {
            document.getElementById("disc-logo").classList.add("loading");
            break;
        }

        case Topic.SEARCH.FINISHED: {
            document.getElementById("disc-logo").classList.remove("loading");
            break;
        }
    }
}

function songTopic(response, storageRef) {
    switch (response.type) {
        case Topic.SONGS.UPDATE: {
            const id = storageRef.current.songs.findIndex(song => song.song_id === response.song.song_id);
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
    }
}

function clearSongs(storage) {
    storage.webSocket.send(JSON.stringify({
        request: Topic.DISC.CLEAR
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
                    request: Topic.USER.LOGIN,
                    topic: Topic.DESCRIPTORS.USER,
                    username: username
                }));
                ping();
            };

            storage.webSocket.onmessage = (event) => {
                const response = JSON.parse(event.data);

                switch (findTopic(response.topic).toLowerCase()) {
                    case Topic.DESCRIPTORS.DISC: {
                        discTopic(response, storageRef);
                        break;
                    }

                    case Topic.DESCRIPTORS.SEARCH: {
                        searchTopic(response, storageRef);
                        break
                    }

                    case Topic.DESCRIPTORS.SONGS: {
                        songTopic(response, storageRef);
                        break
                    }

                    case null: {
                        console.error("Invalid request: ", response);
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
                storageRef.current.webSocket.send(JSON.stringify({
                    request: Topic.USER.PING,
                    topic: Topic.DESCRIPTORS.USER
                }));
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
