# --- Build stage ---

From node:20.19-alpine AS build
WORKDIR /app

# 1) copy only manifeds to leverage Docker cache
#COPY package.json package-lock.json* pnpm-lock.yml* yarn.lock* ./
COPY package.json package-lock.json* ./


# 2) Install deps (ci if lock exists; fallback to install)
RUN npm ci || npm install

# 3) Copy the rest and build
COPY . .
RUN npm run build



# --- runtime stage (nginx) ---
FROM nginx:1.29.2-alpine

# copy our nginx config (keep this path in our repo)
COPY nginx/default.conf /etc/nginx/conf.d/default.conf

# copy the build frontend
COPY --from=build /app/dist /user/share/nginx/html



#Note: if you find seomthing that is not working or is questionable, please just let me know
#    Lg Mike