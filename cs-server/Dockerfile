FROM alpine:latest
WORKDIR /opt/cangling/sir
COPY SirServer ./
RUN chmod +x /opt/cangling/sir/SirServer
VOLUME repo
EXPOSE 8080

ENTRYPOINT /opt/cangling/sir/SirServer -r /repo