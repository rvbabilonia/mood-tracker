FROM node:latest

COPY dist/mood-tracker-web/. /app/mood-tracker-web/

ENV TZ Pacific/Auckland

# proxy settings
#RUN npm config set proxy http://hostname:port
#RUN npm config set https-proxy http://hostname:port
RUN npm install -g http-server

ENTRYPOINT ["http-server", "/app/mood-tracker-web", "--port", "4200"]

EXPOSE 4200
