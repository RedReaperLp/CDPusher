    const data = await fetch('/api/ean/' + ean)
        .then(response => response.json())
        .catch(error => console.error(error));
    console.log(data.tracks[0].artists);
    return data;
}

export default fetchEAN;