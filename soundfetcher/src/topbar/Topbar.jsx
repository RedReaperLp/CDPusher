import "./topbar.css"
import LeftBAR from "./parts/LeftBAR"
import CenterBAR from "./parts/CenterBAR"
import RightBAR from "./parts/RightBAR"
function Topbar() {
    return (
        <>
            <div className="topbar">
                <LeftBAR />
                <CenterBAR />
                <RightBAR />
            </div>
        </>
    )
}

export default Topbar