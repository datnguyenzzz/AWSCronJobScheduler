import React, { FC } from "react"

type Props = {
    name: string,
    url: string
}

const Pokemon: FC<Props> = ({name, url}) => {
    return (
        <p>{name}</p>
    )
}

export default Pokemon
