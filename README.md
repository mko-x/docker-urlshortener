# docker-urlshortener
Shorten urls using cassandra database.

#Installation

##Cassandra
```
docker run -d --name cassandra mkodockx/docker-cassandra
```
Have a look at [i-l.li/shortdock](http://i-l.li/shortdock)

##UrlShortener
```
docker run --name shurl -d --link cassandra:cassandra -p 80:8080 mkodockx/docker-urlshortener
```
#Database configuration

##Cassandra

ShUrl will use the Keyspace with the name "shurl".
If it doesn't exist yet it will be created with the following settings:
```
CREATE KEYSPACE IF NOT EXISTS shurl WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
}
```
In any non-testing environment the Keyspace should probably be pre-created.

##More info
Based on work by [Michael Krolikowski](https://github.com/mkroli)
