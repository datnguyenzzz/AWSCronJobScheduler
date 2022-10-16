import React, { FC } from "react"
import { Route, Routes } from "react-router-dom"
import Home from "../Home/Home"
import Pokedex from "../Pokedex/Pokedex"

const Layout: FC<{}> = () => {
    return (
        <section>
            <div>
                <Routes>
                    <Route path="/home" element={<Home/>}/>
                    <Route path="/funny/pokedex" element = {<Pokedex/>} />
                </Routes>
            </div>
        </section>
    )
}

export default Layout
