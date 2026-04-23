# w2153025_SmartCampus-Sensor-API
A RESTful API for Smart Campus sensor and resource management, built with Java JAX-RS (Jersey), Jackson, and Jetty. Supports CRUD operations for campus facilities, rooms, sensors, and sensor readings with custom exception handling and request filtering.

# Smart Campus Sensor Management API - 5COSC022W Coursework

**Student Name:** Kavindu Abeysinghe  
**Student ID:** w2153025/20241210
**Module:** 5COSC022W - Client Server Architectures  
**Coursework Title:** Smart Campus Sensor & Room Management API

## 1. Project Overview

> Important deployment note: use **JDK 17**, unzip this project to a normal local folder such as `C:\Projects\SmartCampusAPI` (not OneDrive), delete any old Tomcat `webapps` folders and `conf/Catalina/localhost/*.xml` files for previous deployments, then **Clean and Build** in NetBeans before running.

This project is a JAX-RS RESTful API built with **Jersey** and packaged as a **Maven WAR project** for easy deployment in **NetBeans + Tomcat**.

The API manages:
- **Rooms**
- **Sensors**
- **Sensor Readings**

It includes:
- Versioned API entry point: `/api/v1`
- Discovery endpoint
- Nested sub-resource for readings
- Structured JSON error responses
- Request/response logging filters
- In-memory data using `ConcurrentHashMap` and `ArrayList`
- Extra work: **JSON snapshot persistence**, **health endpoint**, **stats endpoint**, and **seed reset endpoint**

## 2. Technology Stack

- Java 17
- Maven
- JAX-RS (Jersey 2.41)
- Jackson JSON
- Tomcat 9 in NetBeans
- In-memory collections (no database)

## 3. NetBeans Setup Guide

### Option A - Open directly as Maven Web Project
1. Open **NetBeans**
2. Click **File -> Open Project**
3. Select this folder: `SmartCampusAPI`
4. Let NetBeans download Maven dependencies
5. Configure **Apache Tomcat 9** as the server
6. Clean and Build the project
7. Run the project

### Option B - Run with Maven
```bash
mvn clean package
```

Deploy the generated WAR:
```bash
target/smart-campus-sensor-api.war
```

## 4. Base URL

When deployed on Tomcat, the typical base URL is:

```text
http://localhost:8080/smart-campus-sensor-api/api/v1
```

## 5. Endpoints Summary

### Discovery and extra utility endpoints
- `GET /api/v1`
- `GET /api/v1/health`
- `GET /api/v1/stats`
- `POST /api/v1/admin/reset`

### Rooms
- `GET /api/v1/rooms`
- `POST /api/v1/rooms`
- `GET /api/v1/rooms/{roomId}`
- `DELETE /api/v1/rooms/{roomId}`

### Sensors
- `GET /api/v1/sensors`
- `GET /api/v1/sensors?type=CO2`
- `GET /api/v1/sensors/{sensorId}`
- `POST /api/v1/sensors`

### Sensor readings
- `GET /api/v1/sensors/{sensorId}/readings`
- `POST /api/v1/sensors/{sensorId}/readings`

## 6. Sample cURL Commands

### 1) Discovery endpoint
```bash
curl -X GET http://localhost:8080/smart-campus-sensor-api/api/v1
```

### 2) Get all rooms
```bash
curl -X GET http://localhost:8080/smart-campus-sensor-api/api/v1/rooms
```

### 3) Create a new room
```bash
curl -X POST http://localhost:8080/smart-campus-sensor-api/api/v1/rooms ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"ENG-110\",\"name\":\"Engineering Seminar Hall\",\"capacity\":120}"
```

### 4) Create a sensor linked to an existing room
```bash
curl -X POST http://localhost:8080/smart-campus-sensor-api/api/v1/sensors ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"TEMP-777\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":26.2,\"roomId\":\"ENG-110\"}"
```

### 5) Filter sensors by type
```bash
curl -X GET "http://localhost:8080/smart-campus-sensor-api/api/v1/sensors?type=Temperature"
```

### 6) Get readings for one sensor
```bash
curl -X GET http://localhost:8080/smart-campus-sensor-api/api/v1/sensors/TEMP-001/readings
```

### 7) Add a new reading
```bash
curl -X POST http://localhost:8080/smart-campus-sensor-api/api/v1/sensors/TEMP-001/readings ^
  -H "Content-Type: application/json" ^
  -d "{\"value\":25.1}"
```

### 8) Show 422 for invalid linked room
```bash
curl -X POST http://localhost:8080/smart-campus-sensor-api/api/v1/sensors ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"BAD-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":400,\"roomId\":\"ROOM-XYZ\"}"
```

### 9) Show 403 when posting to a maintenance sensor
```bash
curl -X POST http://localhost:8080/smart-campus-sensor-api/api/v1/sensors/OCC-050/readings ^
  -H "Content-Type: application/json" ^
  -d "{\"value\":18}"
```

### 10) Show 409 when deleting a room that still contains sensors
```bash
curl -X DELETE http://localhost:8080/smart-campus-sensor-api/api/v1/rooms/LIB-301
```

## 7. Example JSON Requests

### Create room
```json
{
  "id": "ENG-110",
  "name": "Engineering Seminar Hall",
  "capacity": 120
}
```

### Create sensor
```json
{
  "id": "TEMP-777",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 26.2,
  "roomId": "ENG-110"
}
```

### Create reading
```json
{
  "value": 25.1
}
```

## 8. Error Handling Implemented

### 409 Conflict
Used when deleting a room that still contains assigned sensors.

### 422 Unprocessable Entity
Used when a client sends valid JSON for a sensor, but the referenced `roomId` does not exist.

### 403 Forbidden
Used when a client tries to add a reading to a sensor that is in `MAINTENANCE` or `OFFLINE`.

### 404 Not Found
Used when a requested room or sensor does not exist.

### 400 Bad Request
Used for invalid IDs, blank fields, or invalid status values.

### 500 Internal Server Error
Used as a global fallback with a safe JSON body and no raw stack traces.

## 9. Extra Work Included

To strengthen the coursework and reduce penalty risk, these extras were added:

1. **Snapshot persistence to JSON file**
   - Data is still stored in memory during runtime.
   - Every change is also saved into `data/smart-campus-state.json`.
   - This helps avoid data loss when restarting the server.

2. **Health endpoint**
   - `GET /api/v1/health`

3. **Statistics endpoint**
   - `GET /api/v1/stats`

4. **Reset endpoint for demos**
   - `POST /api/v1/admin/reset`
   - Useful before video demonstrations or Postman testing.

5. **Extra filters**
   - `roomId` and `status` filters were added to `/sensors`, while keeping the required `type` filter.

## 10. Video Demo Plan

For the Blackboard video, you can show this flow:
1. `GET /api/v1`
2. `GET /rooms`
3. `POST /rooms`
4. `GET /rooms/{id}`
5. `POST /sensors` with valid room
6. `POST /sensors` with invalid room -> 422
7. `GET /sensors?type=Temperature`
8. `GET /sensors/{id}/readings`
9. `POST /sensors/{id}/readings` success
10. `POST /sensors/OCC-050/readings` -> 403
11. `DELETE /rooms/LIB-301` -> 409
12. `GET /stats`

---

# 11. Coursework Report Answers

The following are the paraphrased paragraphs:

Part 1.1 - Resource lifecycle
By default, a JAX-RS resource is request-scoped, i.e. each resource is typically instantiated on a request. This implicitly guards against accidentally putting mutable state in resources. Therefore, it is inadvisable to store application data in resource instance fields. In this project, application data is stored in a singleton store class, where a ConcurrentHashMap is used to store application data concurrently, but writes are synchronized to avoid the shared map/list being updated concurrently, causing the resulting race conditions, duplicate write or inconsistent links between rooms, sensors and readings.

Part 1.2 - Hypermedia / HATEOAS
 The advantages of hypermedia are that the clients are directed on possible actions and destinations in an API on-the-fly, rather than being hardcoded routes in a fixed documentation. A discovery response with links to core collections gives the user a sense of ease of exploration of an API that is self-documenting, easily learned, and route-evolvable in comparison to a fixed documentation. This implies a client application would on-the-fly traverse the API and address broken client integrations when API routes are changed.

Part 2.1 - only IDs vs complete room objects.
Sending back only IDs may be useful in minimizing the payload size and bandwidth when the client needs identifiers to perform follow-up operations. Nonetheless, this imposes extra load on the client as they now have to make more HTTP requests to get room properties such as capacity, sensor associations. Full room objects provide a larger payload but is usually favored because there is usually meaningful information that can be easily learned. Whole room objects will be re-sent back here due to convenience and reduced client HTTP calls.

Part 2.2 - DELETE idempotency
 The DELETE operation is also idempotent to the state of the server. Once a successful DELETE has taken place, the resource no longer exists; another request that is the same as the one that succeeded in doing so will not change the server state any more. The initial DELETE will work as the room will be removed but any further similar deletes will give a response of Not Found; in both situations the resource will not be available on the server after the first deletion.

Part 3.1 - @Consumes(JSON) mismatch
 @Consumes(MediaType.The media type is defined as APPLICATION_JSON) and implies that the request should be in the form of the JSON. In the case of a request with a Content-Type header of either text/plain or application/xml, JAX-RS cannot find a suitable message body reader to use with this method, and will reject the request, sending a response of 415 Unsupported Media Type back to the client.

Part 3.2 - The advantages of using query parameters to filter.
Query parameters are natural means of filtering collection resources since they enable clients to narrow the results of the same resource set and not a new sub-resource path. As an example, a query parameter of type sensors?type=CO2 would mean that the set of sensors should be returned, but only the sensors that are of type CO2. Filters are simple to write as well (e.g. "/sensors?roomId=1 and type=CO2) and extend (e.g. Add more filters such as 'status') than having path parameters where there is little room to be flexible (e.g. "/sensors/type/CO2).

Part 4.1 - Advantages of sub-resource locator pattern.
The sub-resource locator pattern makes the API easier because it decouples functionality to a separate class. The other option of having all room/reading endpoints in the sensor controller renders the sensor controller difficult to read, maintain and test.By passing the accountability of readings to a sub-resource locator class (SensorReadingResource, here) it is possible to achieve more targeted controller classes, simpler extensibility, and enhanced testability in complex APIs.

Part 4.3 - Consistency of historical data.
 Whenever a new sensor reading is written to the store the value of the currentValue in the Sensor object is also updated. This makes sure that the current and historical data is consistent: a client may want to look at the current currentValue of the sensor data, rather than calculating it based on all of the historical readings.

Part 5.1 - Why 422 is better than 404 here
 This is a better semantically correct response in this case because the body of the request is syntactically correct JSON; but it is just a single value in the body, i.e. roomId, referencing an object that cannot be resolved. This suggests that the user is trying to do something valid but is providing invalid content in the body and a 404 would mean that the resource being requested is not on the server.

Part 5.2.1 - The dangers of exposing stack traces.
 A response body that is a Java stack trace gives the client sensitive information about the internal functioning of the application, including the names of Java packages of classes, names of classes, names of methods, version numbers of internal libraries and file system paths that were used in development of the application. Attackers can use this information to search known vulnerabilities or map the internal structure of the application in more detail. These are concealed by giving a simple response of 500.

Part 5.3 - Reasons filters are preferable to logging.
 Cross-cutting concerns should be handled by filters because they can be used on all incoming requests, or outgoing responses of any of the JAX-RS API. This makes sure that there are no log entries missed, rather than making each resource class write Logger.info() calls, you can write log messages once in a filter. The filters also provide a simpler to manage, centrally controllable log service, as opposed to an array of redundant logging code scattered all over the application.

NetBeans/Tomcat Notes
- Recommended JDK: 17. It is evaluated with Java 17 and warnings can be shown in the build when usingJDK 26 within NetBeans (this is a UI bug) but the project can still be packaged correctly.
- Prior to initial deployment, remove any stale Tomcat descriptors in tomcat/conf/Catalina/localhost/ (e.g. smart-campus-api.xml or smart-campus-sensor-api.xml).
- In case of old-path errors, shut down Tomcat and remove old XML descriptors in tomcat/conf/Catalina/localhost/ and then remove the tomcat/webapps directory. Then re-deploy NetBeans.
- App URL: http://localhost:8080/smart-campus-sensor-api/api/v1
