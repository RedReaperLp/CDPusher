import {useEffect, useMemo, useRef, useState} from "react";
import Topbar from "./topbar/Topbar.jsx";
import Content from "./main/Content.jsx";
import Footer from "./footer/Footer.jsx";
import {fetchSongs} from "./Fetching.jsx";

function App() {
    const [songs, setSongs] = useState([]);
    const ws = useMemo(() => new WebSocket('wss://redreaperlp.de/api/ws/'), []);
    const username = "RerLp";
    const storageRef = useRef({
        songs: songs,
        setSongs: setSongs,
        webSocket: ws
    });


    useEffect(() => {
        const storage = storageRef.current;

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
                case "song-response":
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
    }, []);

    useEffect(() => {
        storageRef.current = {
            setSongs: setSongs,
            songs: songs,
            webSocket: storageRef.current.webSocket
        }
        setRender(!render);
    }, [songs]);

    useEffect(() => {
        fetchSongs(username).then(songs => {
            setSongs(songs);
        });
    }, []);

    function ping() {
        setInterval(() => {
            storageRef.current.webSocket.send(JSON.stringify({request: "ping"}));
        }, 5000);
    }

    const [render, setRender] = useState(false);

    return (<div className={"container"}>
        <Topbar storage={storageRef.current}/>
        <Content storage={storageRef.current}/>
        <Footer storage={storageRef.current}/>
    </div>);
}

export default App;
