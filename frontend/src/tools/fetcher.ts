const fetcher = (url: string) => {
    return fetch(url).then((res) => {
        if (res.ok) {
            return res.json()
        }

        return {
            error: true
        }
    })
}

export default fetcher
