import React, { FC, Suspense, useEffect, useState } from "react"
import useSWR from "swr"

import styles from "./Pokemon.module.css"

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

    if (data === null || data === undefined || error || data.error) {
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

    const cardColorStyle = {
        background: `var(--${pokemonTypes[0]})`
    }

    return (
        <div className={styles.style_card} style={cardColorStyle}>
            <div className={styles.styled_card_header}>
                <h2>{name}</h2>
                <button onClick={onSwitchImage}>
                    {
                        shiny ? "Normal" : "Shiny"
                    }
                </button>
                <div>#{id}</div>
            </div>

            <img alt={name} src={image} />

            <div className={styles.styled_card_typeList}>
                {pokemonTypes.map((pokemonType:string) => {

                    const individualColorType = {
                        color: `var(--${pokemonType})`
                    }
                    return (
                        <span key={pokemonType}
                            className={styles.styled_card_type}
                            style={individualColorType}>
                            {pokemonType}
                        </span>
                    )
                })}
            </div>

        </div>
    )
}

export default Pokemon
