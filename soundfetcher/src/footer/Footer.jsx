function Footer(storage) {
    return (
        <div className={"footer"}>
            <a>© 2024 RedReaperLp</a>
            <button onClick={() => {
                storage.webSocket.send({
                    request: "clear-songs"
                })
                storage.songs.set([]);
            }}>Clear Songs</button>
        </div>
    );
}

export default Footer;