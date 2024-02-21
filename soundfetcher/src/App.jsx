import Topbar from "./topbar/Topbar.jsx";
import Content from "./main/Content.jsx";
import {useEffect, useState} from "react";
import Footer from "./footer/Footer.jsx";
import useWebSocket from "react-use-websocket";
import {fetchSongs} from "./Fetching.jsx";

function App() {
    const [songs, setSongs] = useState([]);
    const {sendJsonMessage, lastJsonMessage, readyState} = useWebSocket('wss://redreaperlp.de/api/ws/', {
        shouldReconnect: () => true, share: true
    });

    const [storage, setStorage] = useState({
        songs: {
            set: (value) => {
                setSongs(value);
            }, get: () => {
                return songs;
            }
        }, webSocket: {
            get: () => {
                return lastJsonMessage;
            }, send: (value) => {
                sendJsonMessage(value);
            }
        }
    });

    const createInterval = () => {
        let intervalId;
        const sendPing = () => {
            sendJsonMessage({
                request: 'ping'
            });
        };

        intervalId = setInterval(sendPing, 1000 * 5);

        const stopInterval = () => {
            clearInterval(intervalId);
        };

        sendPing();
        return stopInterval;
    };

    useEffect(() => {
        let stopInterval;

        if (readyState === 1) {
            sendJsonMessage({
                request: "login",
                username: "RedReaperLp",
            });
            if (!stopInterval) {
                stopInterval = createInterval();
            }
        }
        return () => {
            if (stopInterval) {
                stopInterval();
            }
        };
    }, [readyState]);


    useEffect(() => {
        fetchSongs("RedReaperLp").then(songs => {
            setSongs(songs);
        });
    }, []);

    useEffect(() => {
        setStorage(prevState => ({
            ...prevState,
            songs: {set: (value) => setSongs(value), get: () => songs}
        }));
    }, [songs]);


    useEffect(() => {
        if (lastJsonMessage) {
            const object = lastJsonMessage;
            switch (object.request) {
                case "song-repsonse":
                    setSongs(prevState => {
                        if (prevState.find(song => song.trackID === object.song.trackID) === undefined) {
                            prevState.push(object.song);
                        }
                        return prevState;
                    });
                    break;
                case "song-update":
                    setSongs(prevState => {
                        const index = prevState.findIndex(song => song.trackID === object.song.trackID);
                        if (index !== -1) {
                            prevState[index] = object.song;
                        }
                        return prevState;
                    });
                    break;
                default:
                    console.log("Unknown request", object);
            }
        }
    }, [lastJsonMessage])


    return (<div className={"container"}>
        <Topbar storage={storage}/>
        <Content storage={storage}/>
        <Footer storage={storage}/>
    </div>)
}

export default App
