function Footer({storage}) {
    function logout() {
        document.cookie = "username=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        window.location.reload();
    }

    return (
        <div className={"footer"}>
            <a onClick={() => logout()}>© 2024 RedReaperLp</a>
        </div>
    );
}

export default Footer;