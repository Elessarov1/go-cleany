# syntax=docker/dockerfile:1.7

FROM node:22.23.2-alpine3.24 AS build

WORKDIR /app/clients/typescript-fetch

COPY clients/typescript-fetch/package.json clients/typescript-fetch/package-lock.json ./
RUN --mount=type=cache,target=/root/.npm npm ci --ignore-scripts

COPY clients/typescript-fetch/ ./
RUN npm run build

WORKDIR /app/frontend

COPY frontend/package.json frontend/package-lock.json ./
RUN --mount=type=cache,target=/root/.npm npm ci

COPY frontend/ ./

ARG VITE_API_BASE_URL=/
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ARG VITE_PREVIEW_MODE=false
ENV VITE_PREVIEW_MODE=${VITE_PREVIEW_MODE}
ARG VITE_TELEGRAM_BOT_USERNAME=go_cleany_bot
ENV VITE_TELEGRAM_BOT_USERNAME=${VITE_TELEGRAM_BOT_USERNAME}

RUN npm run build

FROM caddy:2.11.4-alpine

COPY frontend/Caddyfile /etc/caddy/Caddyfile
COPY --from=build /app/frontend/dist /srv

EXPOSE 80
