export async function fetchSongs(username) {
    return await fetch("/api/user/" + username, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then(data => {
            for (let i = 0; i < data.length; i++) {
                if (!data[i].coverURI) {
                    data[i].coverURI = "/assets/images/svg/questionmark.svg";
                }
            }
            return data;
        })
        .catch(error => console.error(error));
}