import "./Content.scss";
import Popup from "./popup/Popup.jsx";
import {useState} from "react";

function Content({storage}) {
    function testEans() {
        const eans = ["0 600753 962565", "4 053804 318761", "5 099920 596323", "5 099748 744425"]
        return eans.map((ean) => {
            return (
                <div key={ean} onClick={() => {
                    storage.webSocket.send(({
                        request: "search",
                        ean: ean
                    }))
                }}>
                    <a>{ean} - - - - Click to Fetch
                        songs {ean === "5 099920 596323" && "- Click to see what happens when the CD is not documented well enough"} </a>
                </div>
            )
        })
    }

    const [popup, setPopup] = useState(false);

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
            {storage.songs.get() && storage.songs.get().map((song) => {
                let className = "song";
                if (song.spotifyMissmatch || !song.spotifySearch) {
                    className += " song__missmatch";
                }
                if (song.coverURI === null) {
                    song.coverURI = "https://redreaperlp.de/assets/images/svg/questionmark.svg";
                }
                return (
                    <div className={className} key={song.trackID} onClick={() => openPopup(song)}>
                        <div className="title__cover">
                            <img className={"song__cover"} height={"40px"} width={"40px"}
                                 src={song.coverURI}
                                 alt="cover"/>
                            <a className={"song__title"}>{song.title}</a>
                        </div>
                        <a className={"song__artist"}>{song.artists && song.artists[0]}</a>
                        <a className={"song__album"}>{song.album}</a>
                    </div>
                )
            })}
        </div>
    )
}

export default Content