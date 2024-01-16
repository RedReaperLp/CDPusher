async function fetchEAN(ean) {
    const data = await fetch('http://redreaperlp.de/api/ean/' + ean)
        .then(response => response.json())
        .catch(error => console.error(error));
    console.log(data.tracks[0].artists);
    return data;
}

export default fetchEAN;