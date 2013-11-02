Distributed Map-Reduce Framework
================================

##Objective
##Directory Structure
##Compile and Run Instructions
##Process
Once the server is running and waiting to do the mapping and reducing, the user can start
the client application. The client application asks what kind of map reduce job needs to
be done, and proceeds to send this information to a remote actor, \Master Actor".
The master actor reads what type of job it is, and sends the appropriate map function to
the MapActor. The map receive this function, and calls it. Once the mapper has nished
its work it sends the data to the ReduceActor, along with the reduce function.
The Reduce Actor completes the work and sends the nal results to the Master Actor. The
Master Actor should send it back to the client where it will be displayed on the console.
However, I have the Master Actor printing the results at this point.
All of the map functions are dened in the master actor class and the reduce functions
are dened in the map actor class. A round-robin router is used to generate and control
the map and reduce actors. The scalability will rely, in some part, on the hardware of the
computer running the application.
###Map and Reduce Functions
###Generality of Framework
###Scalability 
##Issues/TODO

