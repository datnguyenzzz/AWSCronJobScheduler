import React, { FC } from "react"
import Layout from "../Layout/Layout"
import Sidenav from "../Sidenav/Sidenav"

const App: FC<{}> = () => {
    return (
        <>
            <Sidenav/>
            <Layout/>
        </>
    )
}

export default App
