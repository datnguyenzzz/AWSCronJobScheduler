FROM node:16.17.1-alpine3.15
RUN mkdir -p /frontend
WORKDIR /frontend

#set up env path
ENV PATH /frontend/node_modules/.bin:$PATH

#install
RUN npm install serve
RUN npm install --save-dev eslint-plugin-react
RUN npm install --save-dev react-minimal-side-navigation
RUN npm install --save-dev swr react-loading-skeleton styled-components
RUN npm install --save-dev typescript-plugin-css-modules

#build own webpack
RUN npm install --save-dev webpack webpack-cli webpack-dev-server @types/node @types/webpack
# for babel
RUN npm install --save-dev @babel/core @babel/preset-env @babel/preset-react ts-loader
# for css loader
RUN npm install --save-dev style-loader css-loader mini-css-extract-plugin
# make TS work with webpack
RUN npm install --save-dev html-webpack-plugin fork-ts-checker-webpack-plugin typescript
# deploy build bundle
RUN npm install --save-dev html-webpack-plugin

COPY /build /frontend/build
