import fetchEAN from "../../Fetching.jsx";

function LeftBAR() {
    function evalueate(props) {
        if (event.key === "Enter") {
            fetchEAN(props.target.value);
        }
    }

    return (
        <div className="left-bar">
            <div className="left-bar__logo centering40" style={{
                paddingRight: "10px",
            }}>
                <img height={"40px"} width={"40px"} src={"https://www.svgrepo.com/show/880/compact-disc.svg"}
                     alt="logo"/>
            </div>
            <div className="left-bar__search centering40">
                <input onKeyDown={(props) => evalueate(props)} type="text" placeholder="Search"/>
            </div>
        </div>
    );
}

export default LeftBAR;