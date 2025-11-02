### Assumptions
**Client Initial Position:**
- When a client connects, they are assumed to already be on the elevator
- Clients only choose their destination floor, not their starting floor
 **Floor Traversal:**
- To move from floor a to z, the elevator passes through all intermediate floors
**Single Elevator Configuration:**
- Only 1 elevator exists in this system

### Features:
**Client-Server Architecture:**
- Server (Server)
	- Maintains current elevator floor and direction
	- Keeps track of pending floor requests
	- Sends elevator state updates to clients
- Client (Client)
	- Sends destination floor requests to the server
	- Receives and prints elevator status
**Elevator Scheduling**:
- SCAN Algorithm
	- Elevator moves in one direction serving all requests until it reaches the end
	- Reverses direction and services all request in the opposite direction
### Features not implemented:
**Multiple Elevators:**
- System currently supports only one elevator
- Multi-elevator coordination is not implemented
**Client Starting Floor Selection:**
- Clients cannot specify a starting floor
- They all are assumed to be already on the elevator.
**Edge Cases**:
- No priority requests

### How To Run:

**Java Version Used:** 17.0.15

**Client**: ```java -jar Client.jar```

**Server**: ```java -jar Server.jar```
