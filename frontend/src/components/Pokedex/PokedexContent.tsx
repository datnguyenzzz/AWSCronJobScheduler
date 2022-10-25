import React, { FC, Suspense } from "react"
import useSWR from "swr"

const END_POINT: string = 'https://pokeapi.co/api/v2/'

const getPokemonApi = (limit: number): string => {
    return [END_POINT, 'pokemon?limit=', limit.toString()].join('')
}

type Props = {
    pokemonLimit: number
}

type PokemonType = {
    name: string,
    url: string
}

const PokedexContent: FC<Props> = ({ pokemonLimit }) => {

    //get inside result json
    const { data:{results} } = useSWR(getPokemonApi(pokemonLimit))

    return (
        <>
            {console.log(results)}
            {results.map((pokemon: PokemonType, index:number) => {
                return (
                    <Suspense key={index} fallback = {<p>Loading...</p>}>
                        <p key={pokemon.name}>{pokemon.name}</p>
                    </Suspense>
                )
            })}
        </>
    )
}

export default PokedexContent
