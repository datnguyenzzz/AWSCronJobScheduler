import React from 'react'
import ReactDOM from 'react-dom/client'
import Pokedex from './components/Pokedex/Pokedex'
import Sidenav from './components/Sidenav/Sidenav'
import reportWebVitals from './reportWebVitals'

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement)
root.render(
    <>
        <Sidenav/>
    </>
)

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals()
