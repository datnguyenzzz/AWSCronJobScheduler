FROM node:16.17.1-alpine3.15
RUN mkdir -p /frontend
WORKDIR /frontend

#set up env path
ENV PATH /frontend/node_modules/.bin:$PATH

RUN npm install serve
COPY /build /frontend/build
