# Server Application in Java

## A project at Systems Programming Course at Ben-Gurion University
The implementation of the server is based on the Reactor and Thread-Per-Client
(TPC) servers.<br/>
Any time the server receives a message from a client it can replay back
to the client itself.<br/>

## Establishing a client/server connection
Upon connecting, a client must specify their username using a Login command.<br/> 
The nickname must be unique and cannot be changed after it is set.<br/> 
Once the command is sent, the server will reply on the validity of the username.<br/> 
Once a user is logged in successfully, he can submit other commands.<br/> 

## Using Extended TFTP 
The extended TFTP Specification The TFTP (Trivial File Transfer Protocol) allows users to upload and download files from a given server. <br/>
The extended version will require a user to perform a passwordless server login as well as enable the
server to communicate broadcast messages to all users and support for directory listings.<br/>
It is a binary protocol (non-text-base).<br/> 
Binary protocols are very efficient when it comes to reducing the bandwidth used.<br/> 
The commands are defined by an opcode that describes the incoming command.<br/> 
For each command, a different length of data needs to be read according to it’s specifications.<br/>

## Supported Commands
The extended TFTP supports various commands needed in order receive and upload files.
There are two types of commands, Server-to-Client and Client-to-Server. The commands
begin with 2 bytes (short) to describe the opcode. The rest of the message will be defined
specifically for each command as such:<br/>
2 bytes Length defined by command.

## Client-to-Server packets
The extended TFTP supports 10 types of packets: <br/>
 opcode operation <br/>
 1 - Read request (RRQ) <br/>
 2 - Write request (WRQ)  <br/>
 3 - Data (DATA) <br/>
 4 - Acknowledgment (ACK) <br/> 
 5 - Error (ERROR)  <br/>
 6 - Directory listing request (DIRQ)  <br/> 
 7 - Login request (LOGRQ) <br/>
 8 - Delete request (DELRQ)  <br/>
 9 - Broadcast file added/deleted (BCAST)  <br/>
10 - Disconnect (DISC)  <br/>


## Connections Interface 
This interface should map a unique ID for each active client connected to the server.<br/> 
**boolean send(int connId, T msg)** – sends a message T to client represented
by the given connId.<br/>
**void broadcast(T msg)** – sends a message T to all active clients.<br/> 
This includes clients that has not yet completed log-in by the extended TFTP
protocol.<br/>
**void disconnect(int connId)** – removes active client connId from map.<br/>

## ConnectionHandler
**void send(T msg)**– sends msg T to the client.<br/> 
Should be used by send and broadcast in the Connections implementation.<br/>

## BidiMessagingProtocol
It exists to support peer 2 peer messaging via the Connections interface.<br/> 
It contains 2 functions:<br/>
**void start(int connectionId,Connections connections)** – initiate the
protocol with the active connections structure of the server and saves the
owner client’s connection id.<br/>
**void process(T message)** – As in MessagingProtocol, processes a given
message. Unlike MessagingProtocol, responses are sent via the
connections object send function.<br/>

