# Server Application in Java

## A project at Systems Programming Course at Ben-Gurion University

The implementation of the server is based on the Reactor and Thread-Per-Client
(TPC) servers.
Any time the server receives a message from a client it can replay back
to the client itself.

## Establishing a client/server connection
Upon connecting, a client must specify their username using a Login command. 
The nickname must be unique and cannot be changed after it is set. 
Once the command is sent, the server will reply on the validity of the username. 
Once a user is logged in successfully, he can submit other commands. 

##Using Extended TFTP 
The extended TFTP Specification The TFTP (Trivial File Transfer
Protocol) allows users to upload and download files from a given server. 
The extended version will require a user to perform a passwordless server login as well as enable the
server to communicate broadcast messages to all users and support for directory listings.
It is a binary protocol (non-text-base). 
Binary protocols are very efficient when it comes to reducing the bandwidth used. 
The commands are defined by an opcode that describes the incoming command. 
For each command, a different length of data needs to be read according to it’s specifications. 


##Structure of command
The extended TFTP supports various commands needed in order receive and upload files.
There are two types of commands, Server-to-Client and Client-to-Server. 
The commands begin with 2 bytes (short) to describe the opcode. The rest of the message will be defined
specifically for each command as such: 
2 bytes Length defined by command

 ------------------------------------------------
 | Opcode | ... |                               |                          
 ------------------------------------------------

#opcode operation
```
 1 Read request (RRQ) 
 2 Write request (WRQ)
 3 Data (DATA)
 4 Acknowledgment (ACK)
 5 Error (ERROR)
 6 Directory listing request (DIRQ)
 7 Login request (LOGRQ)
 8 Delete request (DELRQ)
 9 Broadcast file added/deleted (BCAST)
 10 Disconnect (DISC)
```

## Connections Interface 
This interface should map a unique ID for each active client
connected to the server. 
The implementation of Connections is part of the server pattern and not part of the protocol. 
It has 3 functions that you must implement
(You may add more if needed):
**boolean send(int connId, T msg)** – sends a message T to client represented
by the given connId
**void broadcast(T msg)** – sends a message T to all active clients. This
includes clients that has not yet completed log-in by the extended TFTP
protocol.
**void disconnect(int connId)** – removes active client connId from map.

## ConnectionHandler<T> - A function was added to the existing interface.
**Void send(T msg)** – sends msg T to the client. 
Should be used by send and broadcast in the Connections implementation.

## BidiMessagingProtocol – This interface replacesthe MessagingProtocol interface.
It exists to support peer 2 peer messaging via the Connections interface. It
contains 2 functions:
**void start(int connectionId, Connections<T> connections)** – initiate the
protocol with the active connections structure of the server and saves the
owner client’s connection id.
** void process(T message)** – As in MessagingProtocol, processes a given
message. Unlike MessagingProtocol, responses are sent via the
connections object send function.

