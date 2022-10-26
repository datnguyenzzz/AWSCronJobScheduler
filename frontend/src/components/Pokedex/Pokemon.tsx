import React, { FC } from "react"
import useSWR from "swr"

type Props = {
    name: string,
    url: string
}

type PokemonType = {
    type: string
}

type ApiType = {
    slot: number,
    type: Props
}

const Pokemon: FC<Props> = ({name, url}) => {

    const {data, error} = useSWR(url)

    if (error || data.error) {
        return <div/>
    }

    if (!data) {
        return <div>Loading...</div>
    }

    //extract needed infos
    const { id, sprites, types} = data
    const pokemonTypes: PokemonType[] = types.map((pTypes:ApiType) => (
        pTypes.type.name
    ))

    //console.log(pokemonTypes)
    return (
        <>
            <p>{name}</p>
        </>
    )
}

export default Pokemon
