# upstox_app

How To Set Up

clone this repository using `git clone https://github.com/anuraganand789/upstox_app.git` 
Then go to ohlc project folder and run `mvn compile` to compile the source code
Then run `mvn exec:java` to run the project.

Package Structure
-----------------
All code comes under `com.upstox` package. 
`model` contains the POJO to store json data
`model.event` contains the POJO to represent tick events
`consumer` contains class to compute the ticks after consuming data from ohlc-queue
`queue` contains priority blocking queue to pass data and tick between processes
`ohlc`  contains the class which the entry point of this application
`producer` contains the class which produces OHLCData
`socket` contains code for WebSocketServer, it is not fully implemented
`task`   contains class which can be used to setup timer task in java
`util`   contains classes and functions which can be used by all other classes in this project


About this application
-----------------------

The application works in a producer and consumer relations.
There are two queues and three processes to use it.
One reads data from file and puts into the data-queue.
Then that data is consumed and tick-events are produced for those data and those tick events are pushed into 
tick-queue.
The WebServer process consumes those tick events and sends those to subscribers.

### Producer OHLCProducer

This class reads data from json file.  
Parses the text into json object.  
User that json object to create a OHLCData packet.  
And, that OHLCData packet is pushed into a blocking queue.  
It uses timer, to push data into the queue.  
A delay is calculated based on the current timestamp in the ohlcpacket and timestamp of the start of  
the application. It helos simulate real life delay in the incoming data packets.

### Consumer FiniteStateMachine

This class takes data from the blocking queue.
It maintains the interval start and end for all the stocks.
It goes through a set of stocks and checks for the expiration status of the stocks.
Upon expiration, the bar number is increased and tickevent is sent to a tick-event queue and the tick event is also printed to the console.

### WebSocketServer SocketService
It is supposed to read the tick events form the tick-event queue and send it to the subscribed clients.
But, right now it is not fully implemented.

## Code 
- OHLCApp.java is entry point of this application.
- OHLCData.java is used to store stock data for single events
- EmptyEvent.java is used to show empty event in cases where a stock does not have any trading
- OHLCEvent.java is used to represent an event during a ohlc packet arrival 
- TickEvent.java is an interface, which is implementd by EmptyEvent and OHLCEvent class it provides abstract functions **toJSONString** and a final variable for **ohlc_notify**
- FiniteStateMachine.java contains the logic to process the incoming data. And, push tick events to tick-event queue.
- OHLCBarDataQueue.java is a blocking queue that allows the websocket thread to fetch tick-events. The FinitStateMachine pushes tickevents into this queue
- OHLCPushTask.java is a task class, which is executed by TimerTask
- PacketsBlockingQueue.java is a data queue between OHLCProducer and FiniteStateMachine classes
- OHLCProducer.java reads data from json file and converts data to OHLCData, which is later consumed by FiniteStateMachine





