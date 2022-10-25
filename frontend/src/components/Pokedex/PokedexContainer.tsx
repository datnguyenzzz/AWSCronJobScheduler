import React, { FC, Suspense } from "react"
import PokedexContent from "./PokedexContent"

const PokedexContainer: FC<{}> = () => {
    return (
        <Suspense
            fallback = {<h2>Loading pokedex ...</h2>}>
            <PokedexContent pokemonLimit={150}/>
        </Suspense>
    )
}

export default PokedexContainer
