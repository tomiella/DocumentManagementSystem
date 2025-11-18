# --- build stage ---
FROM node:20.19-alpine AS build
WORKDIR /app

# copy only manifests first for caching
COPY package.json package-lock.json ./

# clean, reproducible install
RUN npm ci && npm cache clean --force

# copy the rest of the sources (after deps cached)
COPY . .

# build (Vite defaults to dist unless configured otherwise)
RUN npm run build

# --- runtime stage ---
FROM nginx:1.29.2-alpine

# nginx config
COPY nginx/default.conf /etc/nginx/conf.d/default.conf

# static assets from Vite
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
