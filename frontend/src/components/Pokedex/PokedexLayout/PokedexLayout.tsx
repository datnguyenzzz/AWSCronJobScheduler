import React, { FC } from "react"
import { SWRConfig } from "swr"
import fetcher from "../../../tools/fetcher"
import PokedexContainer from "../PokedexContainer"
import styles from "./PokedexLayout.module.css"

const PokedexLayout: FC<{}> = () => {
    return (
        <>
            <h1 className={styles.styled_title}> '
                Pokedex
            </h1>
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
