import React, { FC, Suspense } from "react"
import useSWR from "swr"
import Skeleton from "react-loading-skeleton"
import Pokemon from "../Pokemon"
import "react-loading-skeleton/dist/skeleton.css"
import styles from "./PokedexContent.module.css"


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
                            <div className={styles.styled_grid}>
                                <div className={styles.styled_col}>
                                    <Skeleton height={220} width={200}/>
                                </div>
                            </div>
                        }>
                        <Pokemon key={pokemon.name} name={pokemon.name} url = {pokemon.url} />
                    </Suspense>
            ))}
        </>
    )
}

export default PokedexContent
