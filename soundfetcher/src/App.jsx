import Topbar from "./topbar/Topbar.jsx";
import Content from "./main/Content.jsx";
import {useState} from "react";
import Footer from "./footer/Footer.jsx";

function App() {
    const [songs, setSongs] = useState([]);

    const storage = {
        songs: {
            set: (value) => {
                setSongs(value);
            },
            get: () => {
                return songs;
            }
        }
    }

    return (
        <div className={"container"}>
            <Topbar storage={storage}/>
            <Content storage={storage}/>
            <Footer/>
        </div>
    )
}

export default App
