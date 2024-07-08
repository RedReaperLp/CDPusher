import React, {useEffect, lazy, Suspense} from 'react';

const qrcodeRegionId = "html5qr-code-full-region";

const createConfig = () => {
    let config = {};
    config.fps = 5;
    return config;
};

const Html5QrcodeScannerComponent = lazy(() => import("html5-qrcode").then(({Html5QrcodeScanner}) => ({
    default: ({qrcodeRegionId, config, verbose, qrCodeSuccessCallback, qrCodeErrorCallback}) => {
        useEffect(() => {
            const html5QrcodeScanner = new Html5QrcodeScanner(qrcodeRegionId, config, verbose);
            html5QrcodeScanner.render(qrCodeSuccessCallback, qrCodeErrorCallback);

            const element = document.getElementById(qrcodeRegionId);
            if (element) {
                element.style.position = "";
                element.style.padding = "";
                element.style.border = "";
            }

            return () => {
                html5QrcodeScanner.clear().catch(error => {
                    alert("Fehler beim Löschen von html5QrcodeScanner: " + error.message)
                    console.error("Fehler beim Löschen von html5QrcodeScanner: ", error);
                });
            };
        }, []);

        return (
            <div className="scanner_container">
                <div className="scanner" id={qrcodeRegionId}></div>
            </div>
        );
    }
})).catch(error => {
    console.error("Fehler beim Laden von Html5QrcodeScanner: ", error);
}));

const Html5QrcodePlugin = (props) => {
    const config = createConfig(props);
    return (
        <Suspense fallback={<div className="scanner_container">
            <div className={"loading_cam"}>Lade QR-Code
                Scanner...
                <img className={"loading"} src={"https://www.svgrepo.com/show/880/compact-disc.svg"}
                     alt={"loading..."}/>
            </div>
        </div>
        }>
            <Html5QrcodeScannerComponent
                qrcodeRegionId={qrcodeRegionId}
                config={config}
                qrCodeSuccessCallback={props.qrCodeSuccessCallback}
                qrCodeErrorCallback={props.qrCodeErrorCallback}
            />
        </Suspense>
    );
};

const App = (props) => (
    <Html5QrcodePlugin {...props} />
);

export default App;
