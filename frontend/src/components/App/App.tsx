import React, { FC } from "react"
import { Route, Routes } from "react-router-dom"
import Home from "../Home/Home"
import PokedexLayout from "../Pokedex/PokedexLayout/PokedexLayout"
import Sidenav from "../Sidenav/Sidenav"
import styles from "./App.module.css"

const App: FC<{}> = () => {
    return (
        <div className={styles.layout}>

            <Sidenav/>

            <div className={styles.main_content}>
                <Routes>
                    <Route path="/home" element={<Home/>}/>
                    <Route path="/funny/pokedex" element = {<PokedexLayout/>} />
                </Routes>
            </div>

        </div>
    )
}

export default App
