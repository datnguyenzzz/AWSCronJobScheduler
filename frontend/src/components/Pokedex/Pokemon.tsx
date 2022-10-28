import React, { FC, Suspense, useEffect, useState } from "react"
import useSWR from "swr"
import { StyledCard, StyledCardHeader, StyledCardType, StyledCardTypeList } from "./Pokedex.styled"

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
    const [image, setImage] = useState<string>(sprites.front_default)

    const onSwitchImage = () => {
        setImage(process.env.PUBLIC_URL + "/loading.png")
        setShiny(oldShiny => !oldShiny)
    }

    useEffect(() => {
        if (shiny) setImage(sprites.front_shiny)
        else setImage(sprites.front_default)
    }, [shiny])

    //console.log(pokemonTypes[0])
    return (
        <StyledCard pokemonType={pokemonTypes[0]}>
            <StyledCardHeader>
                <h2>{name}</h2>
                <button onClick={onSwitchImage}>
                    {
                        shiny ? "Normal" : "Shiny"
                    }
                </button>
                <div>#{id}</div>
            </StyledCardHeader>

            <img alt={name} src={image} />

            <StyledCardTypeList>
                {pokemonTypes.map((pokemonType:string) => (
                    <StyledCardType key={pokemonType} pokemonType={pokemonType}> {pokemonType} </StyledCardType>
                ))}
            </StyledCardTypeList>

        </StyledCard>
    )
}

export default Pokemon
