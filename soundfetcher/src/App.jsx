import {useEffect, useMemo, useRef, useState} from "react";
import Topbar from "./topbar/Topbar.jsx";
import Content from "./main/Content.jsx";
import Footer from "./footer/Footer.jsx";
import {fetchSongs} from "./Fetching.jsx";

function App() {
    const [songs, setSongs] = useState([]);
    const ws = useMemo(() => new WebSocket('wss://redreaperlp.de/api/ws/'), []);

    const storageRef = useRef({
        songs: {
            setSongs: setSongs,
            songs: songs,
        }, webSocket: ws
    });


    useEffect(() => {
        const storage = storageRef.current;

        storage.webSocket.onopen = () => {
            storage.webSocket.send(JSON.stringify({
                request: "login",
                username: "RedReaperLp",
            }));
            ping();
        };

        storage.webSocket.onmessage = (event) => {
            const object = JSON.parse(event.data);
            const curSongs = storageRef.current.songs;
            console.log(storageRef.current.songs.songs);
            switch (object.request) {
                case "song-response":
                    const id = curSongs.songs.findIndex(song => song.trackID === object.song.trackID);
                    if (id !== -1) {
                        curSongs.setSongs(prevSongs => {
                            const updatedSongs = [...prevSongs];
                            updatedSongs[id] = object.song;
                            return updatedSongs;
                        });
                    } else {
                        curSongs.setSongs(prevSongs => [...prevSongs, object.song]);
                    }
                    break;
                case "song-update":
                    curSongs.setSongs(prevSongs => {
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
            songs: {
                setSongs: setSongs,
                songs: songs,
            }, webSocket: storageRef.current.webSocket
        }
        setRender(!render);
    }, [songs]);

    useEffect(() => {
        fetchSongs("RedReaperLp").then(songs => {
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
