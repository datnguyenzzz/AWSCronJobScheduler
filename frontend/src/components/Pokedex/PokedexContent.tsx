import React, { FC } from "react"

const END_POINT: string = 'https://pokeapi.co/api/v2/'

const getPokemonApi = (limit: number): string => {
    return [END_POINT, 'pokemon?limit=', limit.toString()].join('')
}

const PokedexContent: FC<{}> = () => {

    return (
        <p>{getPokemonApi(10)}</p>
    )
}

export default PokedexContent
