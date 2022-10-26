import React, { FC } from "react"
import useSWR from "swr"
import { StyledCard } from "./Pokedex.styled"

type Props = {
    name: string,
    url: string
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
    const pokemonTypes: string[] = types.map((pTypes:ApiType) => (
        pTypes.type.name
    ))

    console.log(pokemonTypes[0])
    return (
        <StyledCard pokemonType={pokemonTypes[0]}>
            <p>{name}</p>
        </StyledCard>
    )
}

export default Pokemon
