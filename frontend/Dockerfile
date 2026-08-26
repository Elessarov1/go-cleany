# syntax=docker/dockerfile:1.7

FROM node:22.23.2-alpine3.24 AS build

WORKDIR /app

COPY package.json package-lock.json ./
RUN --mount=type=cache,target=/root/.npm npm ci

COPY . .

ARG VITE_API_BASE_URL=/
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ARG VITE_PREVIEW_MODE=false
ENV VITE_PREVIEW_MODE=${VITE_PREVIEW_MODE}

RUN npm run build

FROM nginx:1.30.4-alpine3.24

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
