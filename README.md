# Cangling Server

Cangling Server is a map server.

# release a new version
```shell
  ./release.sh
```

# start a server local
```shell
  ./SirServer serve -r /path/to/repository/root -o -p8080 
```

# command helper
```shell
  ./SirServer -h
  ./SirServer serve -h
```

# docker start
```shell
   docker run  -p8080:8080  -v ./reporoor:/repo mapway/sir-server:1.0
```

# Example

![./doc/images/demo.png](./doc/images/demo.png)

