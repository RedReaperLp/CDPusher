async function fetchEAN(ean) {
    return await fetch('/api/ean/' + ean)
        .then(response => response.json())
        .catch(error => console.error(error));
}

export default fetchEAN;