import "./Content.scss";
import {Popup} from "./popup/Popup.jsx";
import {useEffect, useState} from "react";
import {Topic} from "../Topic.js";

function Content({storage}) {
    function showInfo() {
        return (
            <>
                <div>
                    <ul>
                        <li>
                            <a>To search a song, either use your smartphone to scan a Barcode on this site</a><br/>
                            <ul>
                                <li>
                                    <a>Note that you need to use the same Username, if you don't know the username
                                    anymore,<br/>click
                                    on the footer to log out</a><br/>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <a>Otherwise you can enter the Ean manually in the field at the top left corner</a>
                        </li>
                    </ul>
                </div>
            </>
        )
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
            {storage.songs.length == 0 && showInfo()}
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