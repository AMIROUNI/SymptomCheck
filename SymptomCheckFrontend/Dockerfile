# ---------- STAGE 1: Build the Angular app ----------
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install --force

COPY . .

RUN npm run build -- --configuration production

# ---------- STAGE 2: Serve with Nginx ----------
FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/*

COPY --from=build /app/dist/doc-link-angular/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
