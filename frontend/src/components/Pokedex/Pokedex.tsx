import React, { FC } from "react"
import { SWRConfig } from "swr"
import fetcher from "../../tools/fetcher"
import { StyledTitle } from "./Pokedex.styled"
import PokedexContainer from "./PokedexContainer"

const Pokedex: FC<{}> = () => {
    return (
        <>
            <StyledTitle> Pokedex </StyledTitle>
            <SWRConfig
                value = {{
                    fetcher,
                    suspense: true,
                }}>
                <PokedexContainer />
            </SWRConfig>
        </>
    )
}

export default Pokedex
