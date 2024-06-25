import "./Content.scss";
import {Popup} from "./popup/Popup.jsx";
import {useEffect, useState} from "react";
import {Topic} from "../Topic.js";

function Content({storage}) {
    function testEans() {
        const eans = ["0 600753 962565", "4 053804 318761", "5 099920 596323", "5 099748 744425"]
        return eans.map((ean) => {
            return (
                <div key={ean} onClick={() => {
                    storage.webSocket.send(JSON.stringify(({
                        request: Topic.SEARCH.START,
                        topic: Topic.DESCRIPTORS.SEARCH,
                        ean: ean
                    })))
                }}>
                    <a>{ean} - - - - Click to Fetch
                        songs {ean === "5 099920 596323" && "- Click to see what happens when the CD is not documented well enough"} </a>
                </div>
            )
        })
    }

    const [popup, setPopup] = useState({});

    function openPopup(song) {
        if (song === popup) {
            closePopup();
            return;
        }
        setPopup(song);
    }

    useEffect(() => {
        document.body.addEventListener("keydown", (event) => {
            if (event.key === "Escape") {
                closePopup();
            }
        })
    }, []);

    function closePopup() {
        setPopup(false);
    }

    return (
        <div className={"content"}>
            {popup && <Popup song={popup} storage={storage} closePopup={() => closePopup()}/>}
            <a>Test EAN's - Optionally use the Searchbar above to search manually</a>
            {testEans()}
            {storage.songs && storage.songs.map((song) => {
                let className = "song";
                if (song.spotify_mismatch || !song.spotify_search) {
                    className += " song__mismatch";
                }
                if (song.cover_uri === null) {
                    song.cover_uri = "/assets/images/svg/questionmark.svg";
                }
                return (
                    <div className={className} key={song.song_id} onClick={() => openPopup(song)}>
                        <div className="title__cover">
                            <img className={"song__cover"} height={"40px"} width={"40px"}
                                 src={song.song_cover_uri}
                                 alt="cover"/>
                            <a className={"song__title"}>{song.song_title}</a>
                        </div>
                        <a className={"song__artist"}>{song.song_artists && song.song_artists.join("\n")}</a>
                        <a className={"song__album"}>{song.song_album}</a>
                    </div>
                )
            })}
        </div>
    )
}

export default Content