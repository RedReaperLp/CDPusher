function Footer({storage}) {
    return (
        <div className={"footer"}>
            <a>© 2024 RedReaperLp</a>
            <button onClick={() => {
                storage.webSocket.send(JSON.stringify({
                    request: "clear-songs"
                }));
                storage.setSongs([]);
            }}>Clear Songs
            </button>
            <button onClick={() => {
                storage.webSocket.send(JSON.stringify({
                    request: "push"
                }));
            }}>Push Songs
            </button>
        </div>
    );
}

export default Footer;