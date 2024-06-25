import {Topic} from "../Topic.js";

function Footer({storage}) {
    return (
        <div className={"footer"}>
            <a>© 2024 RedReaperLp</a>
            <button onClick={() => {
                storage.webSocket.send(JSON.stringify({
                    request: Topic.DISC.CLEAR,
                    topic: Topic.DESCRIPTORS.DISC
                }));
            }}>Clear Songs
            </button>
            <button onClick={() => {
                storage.webSocket.send(JSON.stringify({
                    request: Topic.DISC.PUSH_TO_DB,
                    topic: Topic.DESCRIPTORS.DISC
                }));
            }}>Push Songs
            </button>
        </div>
    );
}

export default Footer;