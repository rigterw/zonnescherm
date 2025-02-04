FROM python:3.11-slim
ARG REPO_URL=https://github.com/<user>/<repo>.git
ARG CONF=config.ini

RUN apt-get update && apt-get install -y git && rm -rf /var/lib/apt/lists/*

WORKDIR /app
RUN git clone $REPO_URL
COPY $CONF ./config.ini
RUN chmod +x start.sh
ENTRYPOINT ["./start.sh"]