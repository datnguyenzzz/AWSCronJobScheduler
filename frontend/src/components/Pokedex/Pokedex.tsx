import React, { FC } from "react"
import { SWRConfig } from "swr"
import fetcher from "../../tools/fetcher"
import { StyledTitle } from "./Pokedex.styled"

const Pokedex: FC<{}> = () => {
    return (
        <>
            <StyledTitle> Pokedex </StyledTitle>
            <SWRConfig
                value = {{
                    fetcher,
                    suspense: true,
                }}>
                <div>Content</div>
            </SWRConfig>
        </>
    )
}

export default Pokedex
