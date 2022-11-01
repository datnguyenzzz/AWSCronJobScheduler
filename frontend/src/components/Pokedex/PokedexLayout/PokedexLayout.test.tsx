import { cleanup, render, RenderResult, screen } from "@testing-library/react"
import React from "react"
import PokedexLayout from "./PokedexLayout"

describe("PokedexLayout component", () => {
    afterAll(cleanup)

    it("Must contains Pokedex header", () => {
        render(<PokedexLayout />)

        const header: string = "Pokedex"
        expect(screen.getByText(header)).toBeInTheDocument()
    })

    it("Title has correct class name", () => {
        render(<PokedexLayout />)

        const header: string = "Pokedex"
        const h1: HTMLElement = screen.getByText(header)
        const expectedClass: string = 'styled_title'
        expect(h1.getAttribute("class")).toContain(expectedClass)
    })

    it("Must has styled container", () => {
        const wrapper: RenderResult = render(<PokedexLayout />)

        const expectedClass: string = 'styled_pokedex'
        expect(wrapper.container.getElementsByClassName(expectedClass).length).toBe(1)
    })
})
