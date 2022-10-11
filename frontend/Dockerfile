FROM node:16.17.1-alpine3.15
RUN mkdir -p /frontend
WORKDIR /frontend
ENV PATH /frontend/node_modules/.bin:$PATH
# install package
COPY package.json /frontend/package.json
RUN npm install 
RUN npm install serve
# copy and build
COPY /public /frontend/public
COPY /src /frontend/src

RUN npm run build