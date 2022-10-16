import React, { FC } from "react"
import { Route, Routes } from "react-router-dom"
import App from "../components/App/App"
import Main from "./Main"

const AppRoute: FC = () => {
    return (
        <Main>
            <Routes>
                <Route path="/*" element={<App/>}/>
            </Routes>
        </Main>
    )
}

export default AppRoute
