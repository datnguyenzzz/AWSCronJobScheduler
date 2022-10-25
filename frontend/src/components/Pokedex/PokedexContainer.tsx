import React, { FC, Suspense } from "react"

const PokedexContainer: FC<{}> = () => {
    return (
        <Suspense
            fallback = {<h2>Loading pokedex ...</h2>}>
            <div>Pokedex content</div>
        </Suspense>
    )
}

export default PokedexContainer
