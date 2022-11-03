import React, { FC, Suspense } from "react"
import PokedexContent from "../PokedexContent/PokedexContent"

const PokedexContainer: FC<{}> = () => {
    return (
        <Suspense
            fallback = {<h2>Loading pokedex ...</h2>}>
            <PokedexContent pokemonLimit={200}/>
        </Suspense>
    )
}

export default PokedexContainer
