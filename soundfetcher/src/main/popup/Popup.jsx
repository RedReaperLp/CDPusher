import Swal from "sweetalert2";
import "./Popup.scss";

export function Popup({song, storage, closePopup}) {
    const id = song.trackID;
    const index = storage.songs.findIndex((song) => song.trackID === id);

    if (index === -1) {
        return (<></>);
    }
    console.log(storage.songs[index]);

    function resolveColor(spotifySearch, spotifyMissmatch) {
        if (!spotifySearch) {
            return "red";
        } else if (spotifyMissmatch) {
            return "yellow";
        } else {
            return "green";
        }
    }

    function fireSwal() {
        window.open("https://open.spotify.com/search/" + song.title + " - " + (song.artists && song.artists[0]))

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
            storage.webSocket.send(
                JSON.stringify({
                    request: "update",
                    uri: value.value,
                    trackID: song.trackID,
                })
            );
        }).then(() => {
            closePopup();
        });
    }

    function resolveButton() {
        if (song.spotifySearch && !song.spotifyMissmatch) {
            return <button className="popup__button" onClick={() => closePopup()}>Close</button>;
        } else {
            return <button className="popup__button" onClick={() => fireSwal()}>Search in Spotify</button>
        }
    }

    return (
        <div className="popup__container">
            <div className="popup__content">
                <div className="popup__cover">
                    <img height="180px" src={song.coverURI === null
                        ? "/assets/images/svg/questionmark.svg"
                        : song.coverURI} width="180px" alt={"Song Cover"}/>
                </div>
                <div className="popup__info">
                    <div className="popup__separator">
                        <div className="popup__row">
                            <div className="popup__upper">
                                <h1 className="popup__title overflow">{song.title}</h1>
                                <p className={"overflow"}><b>Album:</b> {song.album ? song.album : "Unbekannt"}
                                </p>
                            </div>
                            <div className="popup__lower">
                                <p className="release_year">{song.year ? song.year : "Unbekannt"}</p>
                            </div>
                        </div>
                        <div className="popup__row">
                            <div className="popup__upper">
                                <div className="popup__space-around">
                                    <p>CD: {song.discNo}</p>
                                    <p>Track: {song.trackNo}</p>
                                </div>
                            </div>
                            <div className="popup__lower">
                                <p>Interne CD Nummer: {song.internalDiscNo ? song.internalDiscNo : "Unbekannt"}</p>
                            </div>
                        </div>
                    </div>
                    <hr style={{width: "100%"}}/>
                    <div className="popup__separator popup__half-height">
                        <div className="popup__row">
                            <div className="popup__upper">
                                <p className="duration">{Math.floor(song.duration / 60) +
                                    ":" + String.prototype.padStart.call(song.duration % 60, 2, "0") +
                                    " Minuten"}</p>
                            </div>
                        </div>
                        <div className="popup__row">
                            <div className="popup__lower">
                                <p className="composer">Spotify State</p>
                                <div className="popup__indicator"
                                     style={{background: resolveColor(song.spotifySearch, song.spotifyMissmatch)}}></div>
                            </div>
                        </div>
                    </div>
                    <div className="popup__separator popup__button_div popup__half-height popup__full-width popup__center">
                        {resolveButton()}
                    </div>
                </div>
            </div>
        </div>
    );
}