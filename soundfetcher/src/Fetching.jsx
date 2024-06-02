export async function fetchSongs(username) {
    return await fetch("/api/user/" + username, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then(res => {
            const data = res.songs;
            const disc = res.disc;
            for (let i = 0; i < data.length; i++) {
                if (!data[i].song_cover_uri) {
                    data[i].song_cover_uri = "/assets/images/svg/questionmark.svg";
                }
            }
            return {songs: data, disc: disc};
        })
        .catch(error => console.error(error));
}