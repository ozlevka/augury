FROM node:12

WORKDIR /app

COPY package.json server.js /app/

RUN npm install && npm test

CMD ["node", "server.js"]
