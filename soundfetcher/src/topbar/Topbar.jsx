import "./Topbar.scss"
import LeftBAR from "./parts/LeftBAR"
import CenterBAR from "./parts/CenterBAR"
import RightBAR from "./parts/RightBAR"

function Topbar(storage) {
    return (
        <>
            <div className="topbar">
                <LeftBAR storage={storage.storage}/>
                <CenterBAR/>
                <RightBAR/>
            </div>
        </>
    )
}

export default Topbar