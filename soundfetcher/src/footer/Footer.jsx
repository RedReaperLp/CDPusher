function Footer({storage}) {
    return (
        <div className={"footer"}>
            <a>© 2024 RedReaperLp</a>
            <button onClick={() => {
                console.log(storage.webSocket);
                storage.webSocket.send(JSON.stringify({
                    request: "clear-songs"
                }));
                storage.songs.setSongs([]);
            }}>Clear Songs</button>
        </div>
    );
}

export default Footer;