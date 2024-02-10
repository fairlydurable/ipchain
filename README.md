# Chained Microservices Sample

Microservices architectures break down applications to smaller loosely coupled components. This example uses three unrelated APIs and chains them together to produce a meaningful result. It demonstrates how integrating services solves complex problems.

At any point APIs may fail. Services may be unavailable due to load or downtime or they may produce erroneous data. A well-considered retry strategy helps each microservice chain complete as reliably as possible. Isolating each request enables the chains to run at scale.

## Compile
Issue `./compile` from the main directory.

**Prerequisites**
This prototype assumes you have added [`json-java.jar`](https://github.com/stleary/JSON-java) to the `source` subdirectory.

## Run
Issue `./runme`

```
Usage: ./runme <getip | getgeo | getweather>
```

- `getip` - returns IP address.
- `getgeo` - chains the result of `getip` to return approximate latitude and longitude.
- `getweather` - chains the result of `getip` and `getgeo` to return the detailed forecast for the area of the IP's presumed location.

### For example:

```
% ./runme getip
71.56.251.108
% ./runme getgeo
39.5987 -104.7515
% ./runme getweather
Snow likely. Mostly cloudy, with a low around 26.
South southwest wind 6 to 12 mph, with gusts as high
as 16 mph. Chance of precipitation is 60%. New snow
accumulation of 1 to 3 inches possible.
```

### API Sources
This sample uses the following open and free APIs. Please treat them respectfully so they don't go away.

* IPify: [Fetch IP Address](https://www.ipify.org)
* IPAPI: [IP Address to Latitude/Longitude](https://ipapi.co/)
* National Weather Service: [Latitude/Longitude to Forecast](https://www.weather.gov/documentation/services-web-api)