import styled from "styled-components"

// color
export const type: any = {
    bug: '#52BC04',
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
    psychic: '#F112EE',
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
        max-height: 100%;
        max-width: 100%;
    }
`

export const StyledCardHeader = styled.div`
    display: flex;
    justify-content: space-evenly;
    flex-flow: column wrap;

    h2 {
        margin: 5px;
        color: white;
        text-transform: capitalize;
        align-self: center;
    }

    div {
        color: white;
        font-size: 20px;
        font-weight: bold;
        margin-top: 5px;
        align-self: center;
    }

    button {
        align-self: center;
        size: 60%;
    }
`

export const StyledCardTypeList = styled.div`
    display: flex;
    margin-left: 10px;
    margin-bottom: 8px;
    justify-content: space-evenly;
`

export const StyledCardType = styled.span<Props>`
    ${({ pokemonType }) => `
        color: ${type[pokemonType]};
    `}
    background: #3F3E3E;
    display: inline-block;
    font-weight: bold;
    text-transform: capitalize;
    margin-right: 3px;
    padding: 10px;
    align-self: center;
    font-size: 90%;
    border-radius: 20px;
`
