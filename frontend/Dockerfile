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
RUN npm install --save-dev style-loader css-loader
RUN npm install --save-dev typescript-plugin-css-modules

COPY /build /frontend/build
