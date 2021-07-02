FROM debian:buster

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
    && apt-get install -y curl \
        # Install kubectl
    && curl -LO https://storage.googleapis.com/kubernetes-release/release/v1.19.12/bin/linux/amd64/kubectl \
    && chmod +x ./kubectl \
    && mv ./kubectl /usr/bin/ 

