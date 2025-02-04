FROM python:3.11-slim
ARG REPO_URL=https://github.com/<user>/<repo>.git
ARG CONF=config.ini

RUN apt-get update && apt-get install -y git && rm -rf /var/lib/apt/lists/*
RUN apt-get update && apt-get install -y dos2unix

WORKDIR /app
RUN git clone $REPO_URL
COPY $CONF ./config.ini
RUN dos2unix ./start.sh
RUN chmod +x start.sh
RUN git update-index --assume-unchanged start.sh
ENTRYPOINT ["/bin/bash","./start.sh"]