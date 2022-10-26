import React, { FC, useState } from "react"
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

    //view shiny
    const [shiny, setShiny] = useState<boolean>(false)

    //console.log(pokemonTypes[0])
    return (
        <StyledCard pokemonType={pokemonTypes[0]}>
            <div>
                <h2>{name}</h2>
                <button onClick={() => {
                    setShiny(oldShiny => !oldShiny)
                }}> Shiny</button>
                <div>#{id}</div>
            </div>
            {
                !shiny ?
                    <img alt={name} src={sprites.front_default} />
                    : <img alt={name} src={sprites.front_shiny} />
            }

            <div>
                {pokemonTypes.map((pokemonType:string) => (
                    <div key={pokemonType}> {pokemonType} </div>
                ))}
            </div>

        </StyledCard>
    )
}

export default Pokemon
