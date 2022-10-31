import React, { FC } from "react"
import { SWRConfig } from "swr"
import fetcher from "../../../tools/fetcher"
import { StyledPokedex, StyledTitle } from "../Pokedex.styled"
import PokedexContainer from "../PokedexContainer"
import styles from "./PokedexLayout.module.css"

const PokedexLayout: FC<{}> = () => {
    return (
        <>
            <StyledTitle> Pokedex </StyledTitle>
            <SWRConfig
                value = {{
                    fetcher,
                    suspense: true,
                }}>
                <div className={styles.styled_pokedex}>
                    <PokedexContainer />
                </div>
            </SWRConfig>
        </>
    )
}

export default PokedexLayout
