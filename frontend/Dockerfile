From node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* pnpm-lock.yml* yarn.lock* ./
RUN npm ci || npm install
COPY . .
RUN npm run build
