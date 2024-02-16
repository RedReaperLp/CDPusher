import "./Content.scss";
import fetchEAN from "../Fetching.jsx";

function Content({storage}) {
    function testEans() {
        const eans = ["0 600753 962565","4 053804 318761", "5 099920 596323", "5 099748 744425"]
        return eans.map((ean) => {
            return (
                <div key={ean} onClick={(props) => {
                    const text = props.target.innerText;
                    props.target.innerText = "Fetching...";
                    fetchEAN(ean).then((data) => {
                        storage.songs.set(data);
                        props.target.innerText = text;
                    })
                }}>
                    <a>{ean} - - - - Click to Fetch songs {ean === "5 099920 596323" && "- Click to see what happens when the CD is not documented well enough"} </a>
                </div>
            )
        })
    }

    return (
        <div className={"content"}>
            <a>Test EAN's - Optionally use the Searchbar above to search manually</a>
            {testEans()}

            {storage.songs.get().tracks && storage.songs.get().tracks.map((song) => {
                let className = "song";
                if (song.spotifySearchMissMatch || !song.spotifySearch) {
                    className += " song__missmatch";
                }
                return (
                    <div className={className}
                         key={song.trackNumber} onClick={() => console.log(song)}>
                        <div className="title__cover">
                            <img className={"song__cover"} height={"40px"} width={"40px"}
                                 src={song.cover === null ? "/assets/images/svg/questionmark.svg" : song.cover}
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