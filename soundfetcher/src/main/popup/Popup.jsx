import "./Popup.scss"

function Popup({song, storage, closePopup}) {
    const id = song.trackID;
    const index = storage.songs.get().findIndex(song => song.trackID === id);
    if (index === -1) {
        return <div>Song not found</div>
    }

    return (
        <div className={"popup"}>
            <div className={"popup__content"}>
                {leftFlex("title__cover", "Titel & Cover", <><
                    img className={"song__cover"} height={"50px"}
                        width={"50px"}
                        style={{borderRadius: "10px", margin: "10px"}}
                        src={song.coverURI === null ? "/assets/images/svg/questionmark.svg" : song.coverURI}
                        alt="cover"/>
                    <a className={"song__title"}>{song.title}</a></>)}
                {inlineItem("artist", "Artist", song.artists ? song.artists.join(", ")
                    : "Unknown")}
                {inlineItem("album", "Album", song.album ? song.album : "Unknown")}
                {inlineItem("track-number", "Track Nummer", ["CD-Nummer: " + song.discNo,
                    <br key={song.title + song.track}></br>, "Track-Nummer: " + song.trackNo])}
                {inlineItem("duration", "Dauer", [
                    Math.floor(song.duration / 60), ":", String.prototype.padStart.call(song.duration % 60, 2, "0"), " Minuten"])}
                {inlineItem("release-date", "Erscheinungsdatum", song.year ? song.year : "Unknown")}
                {inlineItem("spotify", "Spotify", ["Searched on Spotify: " + (song.spotifySearch ? "Yes" : "No"),
                    <br key={song.title + song.track + "" + song.track}></br>, "Missmatch: " + (song.spotifySearchMissMatch ? "Yes" : "No")])}
                {inlineItem("internal-cd-id", "Interne CD Nummer", song.internalDiscNo ? song.internalDiscNo : "Unknown")}
                {(!song.spotifySearch || song.spotifyMissmatch) && <div className={"spotify__search item"}>
                    <a className={"header__title"}>Search on Spotify:</a>
                    <div style={{display: "flex", flexDirection: "column", alignItems: "center", width: "100%"}}>
                        <a target={"_blank"}
                           href={("https://open.spotify.com/search/" + song.title + " - ") + (song.artists && song.artists[0])}>Click
                            to search</a>
                        <input onKeyDown={(props) => {
                            if (event.key === "Enter") {
                                storage.webSocket.send(({
                                    request: "update",
                                    uri: props.target.value,
                                    trackID: song.trackID
                                }))
                                closePopup();
                            }
                        }} type={"text"} placeholder={"Paste song URL"}></input>
                    </div>
                </div>}
            </div>
        </div>
    )
}

function leftFlex(gridArea, header, children) {
    return (
        <div className="item">
            <a className={"header__title"}>{header}:
            </a>
            <div className={"left-flex"} style={{gridArea: gridArea}}>{children}</div>
        </div>
    )
}

function inlineItem(gridArea, header, children) {
    return (
        <div className="item" style={{gridArea: gridArea}}>
            <a className={"header__title"} style={{marginBottom: "1px"}}>{header}:</a>
            <div className={"inline-item"}
                 style={{gridArea: gridArea, paddingLeft: "5px", paddingTop: "2px"}}>{children}</div>
        </div>
    )
}

export default Popup