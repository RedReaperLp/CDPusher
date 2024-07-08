import Swal from "sweetalert2";
import "./Popup.scss";
import {Topic} from "../../Topic.js";

function useDiscOGs(song, storage, closePopup) {
    storage.webSocket.send(JSON.stringify({
        song_disc_no: song.song_disc_no,
        request: Topic.SONGS.USE_DISCOGS,
        topic: Topic.DESCRIPTORS.SONGS,
        song_track_no: song.song_track_no,
    }))
    closePopup();
}

export function Popup({song, storage, closePopup}) {
    const id = song.song_id;
    const index = storage.songs.findIndex((song) => song.song_id === id);

    if (index === -1) {
        return (<></>);
    }

    function resolveColor(spotifySearch, spotifyMismatch) {
        if (!spotifySearch) {
            return "red";
        } else if (spotifyMismatch) {
            return "yellow";
        } else {
            return "green";
        }
    }

    function fireSwal() {
        window.open("https://open.spotify.com/search/" + song.song_title + " - " + (song.song_artists && song.song_artists[0]))

        Swal.fire({
            title: "Enter Spotify URL",
            input: "url",

            attributes: {
                placeholder: "Enter Spotify URL",
                type: "url",
            },
            showCancelButton: true,
            confirmButtonText: "Submit"
        }).then((value) => {
            if (value === null) {
                return;
            }
            if (value.value === "" || value.value === null) {
                return;
            }
            storage.webSocket.send(
                JSON.stringify({
                    request: Topic.SONGS.UPDATE,
                    topic: Topic.DESCRIPTORS.SONGS,
                    uri: value.value,
                    song_id: song.song_id,
                })
            );
        }).then(() => {
            closePopup();
        });
    }

    function resolveButton() {
        if (song.spotify_search && !song.spotify_mismatch) {
            return <>
                <button className="popup__button" onClick={() => fireSwal()}>Patch</button>
                <button className="popup__button" onClick={() => closePopup()}>Close</button>
            </>
                ;
        } else {
            return <>
                <button className="popup__button" onClick={() => fireSwal()}>Search in Spotify</button>
                <button className="popup__button" onClick={() => useDiscOGs(song, storage, closePopup)}>Use DiscOGs
                </button>
            </>
        }
    }

    return (
        <div className="popup__container">
            <div className="popup__content">
                <div className="popup__cover">
                    <img height="180px" src={song.song_cover_uri === null
                        ? "/assets/images/svg/questionmark.svg"
                        : song.song_cover_uri} width="180px" alt={"Song Cover"}/>
                </div>
                <div className="popup__info">
                    <div className="popup__separator">
                        <div className="popup__row">
                            <div className="popup__upper">
                                <h1 className="popup__title overflow">{song.song_title}</h1>
                                <p className={"overflow"}>
                                    <b>Album:</b> {song.song_album ? song.song_album : "Unbekannt"}
                                </p>
                            </div>
                            <div className="popup__lower">
                                <p className="release_year">{song.song_year ? song.song_year : "Unbekannt"}</p>
                            </div>
                        </div>
                        <div className="popup__row">
                            <div className="popup__upper">
                                <div className="popup__space-around">
                                    <p>CD: {song.song_disc_no}</p>
                                    <p>Track: {song.song_track_no}</p>
                                </div>
                            </div>
                            <div className="popup__lower">
                                <p>Interne CD Nummer: {storage.discInfo.id}</p>
                            </div>
                        </div>
                    </div>
                    <hr style={{width: "100%"}}/>
                    <div className="popup__separator popup__half-height">
                        <div className="popup__row">
                            <div className="popup__upper">
                                <p className="duration">{Math.floor(song.song_duration / 60) +
                                    ":" + String.prototype.padStart.call(song.song_duration % 60, 2, "0") +
                                    " Minuten"}</p>
                            </div>
                        </div>
                        <div className="popup__row">
                            <div className="popup__lower">
                                <p className="composer">Spotify State</p>
                                <div className="popup__indicator"
                                     style={{background: resolveColor(song.spotify_search, song.spotify_mismatch)}}></div>
                            </div>
                        </div>
                    </div>
                    <div
                        className="popup__separator popup__button_div popup__half-height popup__full-width popup__center">
                        {resolveButton()}
                    </div>
                </div>
            </div>
        </div>
    );
}