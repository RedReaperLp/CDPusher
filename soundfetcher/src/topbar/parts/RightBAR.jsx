import {Topic} from "../../Topic.js";

function RightBAR(storage) {
    storage = storage.storage;
    return (
        <div className="right-bar">
            <div className="right-bar__icon">
                <i className="fas fa-user"></i>
            </div>
            <div className="right-bar__icon">
                <i className="fas fa-bell"></i>
            </div>
            <div className="right-bar__icon">
                <i className="fas fa-cog"></i>
            </div>

            {
                storage.songs.length <= 0 ? (<></>) :
                    (<>
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
                    </>)
            }
        </div>
    )
}

export default RightBAR;