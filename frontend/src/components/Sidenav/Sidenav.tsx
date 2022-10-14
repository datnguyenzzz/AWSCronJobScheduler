import React, { FC } from "react"
import { Navigation, NavItemProps } from 'react-minimal-side-navigation'
import 'react-minimal-side-navigation/lib/ReactMinimalSideNavigation.css'

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
                itemId: '/pokedex'
            }
        ]
    }
]

const Sidenav: FC = () => {
    return (
        <>
            <Navigation
                activeItemId="/home"
                onSelect={({itemId}) => {

                }}
                items = {navItems}
            />
        </>
    )
}

export default Sidenav
