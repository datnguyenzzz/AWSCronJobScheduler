import styled from "styled-components"

export const StyledTitle = styled.h1`
    text-align: center;
    size: 20;
`

export const StyledPokedex = styled.div`
    display: flex;
    flex-wrap: wrap;
    flex-flow: row wrap;
    margin: auto;
    width: 90%;
`

// color
const type: any = {
    bug: '#2ADAB1',
    dark: '#636363',
    dragon: '#E9B057',
    electric: '#ffeb5b',
    fairy: '#ffdbdb',
    fighting: '#90a4b5',
    fire: '#F7786B',
    flying: '#E8DCB3',
    ghost: '#755097',
    grass: '#2ADAB1',
    ground: '#dbd3a2',
    ice: '#C8DDEA',
    normal: '#ccc',
    poison: '#cc89ff',
    psychic: '#705548',
    rock: '#b7b7b7',
    steel: '#999',
    water: '#58ABF6'
}


//passed props into css

type Props = {
    pokemonType: string
}

export const StyledCard = styled.div<Props>`
    position: relative;
    ${({ pokemonType }) => `
        background: ${type[pokemonType]} no-repeat;
    `}
    color: #000;
    font-size: 20px;
    border-radius: 20px;
    margin: 5px;
    width: 200px;

    img {
        margin-left: auto;
        margin-right: auto;
        display: block;
    }
`

export const StyledGrid = styled.div`
    display: flex;
    flex-wrap: wrap;
    flex-flow: row wrap;
    div {
        margin-right: 5px;
        margin-bottom: 5px;
    }
`
