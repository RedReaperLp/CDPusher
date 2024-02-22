function LeftBAR({storage}) {
    function evalueate(props) {
        if (event.key === "Enter") {
            storage.webSocket.send(JSON.stringify({
                request: "search",
                ean: props.target.value
            }));
        }
    }

    return (
        <div className="left-bar">
            <div className="left-bar__logo" style={{
                paddingRight: "10px",
                height: "40px",
                width: "40px",
            }}>
                <img height={"40px"} width={"40px"} src={"https://www.svgrepo.com/show/880/compact-disc.svg"}
                     alt="logo"/>
            </div>
            <div className="left-bar__search">
                <input onKeyDown={(props) => evalueate(props)} type="text" placeholder="Enter EAN"/>
            </div>
        </div>
    );
}

export default LeftBAR;