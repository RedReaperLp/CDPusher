import Swal from "sweetalert2";

export function Popup({song, storage, closePopup}) {
    const id = song.trackID;
    const index = storage.songs.findIndex((song) => song.trackID === id);

    if (index === -1) {
        return <div>Song not found</div>;
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
            content: {
                element: "input",
                attributes: {
                    placeholder: "Enter Spotify URL",
                    type: "url",
                },
            },
            buttons: {
                cancel: "Cancel",
                confirm: "Submit",
            },
        }).then((value) => {
            if (value === null) {
                return;
            }
            storage.webSocket.send(
                JSON.stringify({
                    request: "update",
                    uri: value,
                    trackID: song.trackID,
                })
            );
        }).then(() => {
            closePopup();
        });
    }

    return (
        <div className="popup">
            <div className="popup_content">
                <div className="cover">
                    <img height="180px" src={song.coverURI === null
                        ? "/assets/images/svg/questionmark.svg"
                        : song.coverURI} width="180px" alt={"Song Cover"}/>
                </div>
                <div className="content">
                    <div className="content_seperator">
                        <div className="row_div">
                            <div className="upper_div">
                                <h1 className="song__title">{song.title}</h1>
                                <p>Album: {song.album ? song.album : "Unbekannt"}</p>
                            </div>
                            <div className="lower_div">
                                <p className="release_year">{song.year ? song.year : "Unbekannt"}</p>
                            </div>
                        </div>
                        <div className="row_div">
                            <div className="upper_div">
                                <div className="space_around">
                                    <p>CD: {song.discNo}</p>
                                    <p>Track: {song.trackNo}</p>
                                </div>
                            </div>
                            <div className="lower_div">
                                <p>Interne CD Nummer: {song.internalDiscNo ? song.internalDiscNo : "Unbekannt"}</p>
                            </div>
                        </div>
                    </div>
                    <hr style="width: 100%"/>
                    <div className="content_seperator half_h">
                        <div className="row_div">
                            <div className="upper_div">
                                <p className="duration">{Math.floor(song.duration / 60) +
                                    ":" + String.prototype.padStart.call(song.duration % 60, 2, "0") +
                                    " Minuten"}</p>
                            </div>
                        </div>
                        <div className="row_div">
                            <div className="lower_div">
                                <p className="composer">Spotify State</p>
                                <div className="indicator"
                                     style={{background: resolveColor(song.spotifySearch, song.spotifyMissmatch)}}></div>
                            </div>
                        </div>
                    </div>
                    <div className="content_seperator half_h full_w center">
                        <button onClick={() => fireSwal()}>Enter URL</button>
                    </div>
                </div>
            </div>
        </div>
    );
}