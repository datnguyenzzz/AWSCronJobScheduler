import React, { FC } from "react"
import { Navigation, NavItemProps } from 'react-minimal-side-navigation'
import "react-minimal-side-navigation/lib/ReactMinimalSideNavigation.css"
import { useNavigate } from "react-router-dom"

const navItems: NavItemProps[] = [
    {
        title: 'Home',
        itemId: '/home'
    },
    {
        title: 'Funny',
        itemId: '/funny',
        subNav: [
            {
                title: 'Pokedex',
                itemId: '/funny/pokedex'
            }
        ]
    }
]

const Sidenav: FC<{}> = () => {

    const navigate = useNavigate()
    return (
        <div>
            <h1>My app</h1>
            <Navigation
                activeItemId="/home"
                onSelect={({itemId}) => {
                    if (itemId === "/funny") navigate("/")
                    else navigate(itemId)
                }}
                items = {navItems}
            />
        </div>
    )
}

export default Sidenav
