import React, { FC, Suspense } from "react"
import useSWR from "swr"
import { StyledGrid } from "./Pokedex.styled"
import Skeleton from "react-loading-skeleton"
import "react-loading-skeleton/dist/skeleton.css"
import Pokemon from "./Pokemon"

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
            {results.map((pokemon: PokemonType, index:number) => (
                    <Suspense key={index}
                        fallback = {
                            <StyledGrid>
                                <div>
                                    <Skeleton height={200} width={200}/>
                                </div>
                            </StyledGrid>
                        }>
                        <Pokemon key={pokemon.name} name={pokemon.name} url = {pokemon.url} />
                    </Suspense>
            ))}
        </>
    )
}

export default PokedexContent
