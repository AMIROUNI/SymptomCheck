# ---------- STAGE 1: Build the Angular app ----------
FROM node:20-alpine AS build

WORKDIR /app

# Give Node more memory and reduce Angular build workers
ENV NODE_OPTIONS="--max_old_space_size=4096"
ENV NG_BUILD_MAX_WORKERS=2

COPY package*.json ./
RUN npm install --force

COPY . .

# Build Angular in production mode
RUN npm run build -- --configuration production

# ---------- STAGE 2: Serve with Nginx ----------
FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/*

# If your Angular output is dist/doc-link-angular/browser
COPY --from=build /app/dist/doc-link-angular/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
