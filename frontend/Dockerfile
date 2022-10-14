FROM node:16.17.1-alpine3.15
RUN mkdir -p /frontend
WORKDIR /frontend

#set up env path
ENV PATH /frontend/node_modules/.bin:$PATH

#install
RUN npm install serve
RUN npm install --save-dev eslint eslint-config-airbnb eslint-config-prettier eslint-plugin-import eslint-plugin-jsx-a11y eslint-plugin-prettier eslint-plugin-react

COPY /build /frontend/build
