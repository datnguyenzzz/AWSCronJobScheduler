import React, { FC, ReactNode } from "react"

type Props = {
    children: ReactNode
}

/**
 *
 * @param childrean
 * @returns render childrean react node
 */
const Main:FC<Props> = ({ children }) => {
    return (
        <div className="Main">
            {children}
        </div>
    )
}

export default Main
