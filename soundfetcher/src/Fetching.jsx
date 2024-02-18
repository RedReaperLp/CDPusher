export async function fetchSongs(username) {
    return await fetch("https://redreaperlp.de/api/user/" + username, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .catch(error => console.error(error));
}