FROM debian:unstable-slim
WORKDIR /opt/cangling/sir
COPY SirServer /opt/cangling/sir
RUN chmod +x /opt/cangling/sir/SirServer
VOLUME repo
EXPOSE 8080
CMD ["serve", "-r", "/repo"]
ENTRYPOINT ["./SirServer"]

