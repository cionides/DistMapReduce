Distributed Map-Reduce Framework
================================

##Objective
##Directory Structure
The project folder entitled, 'Project' contains this report as well as a subdirectory 'MapRe-
duceFramework' which contains all code for the project. Within said folder, there are two
folders for the client and server code. The application will be run from the client folder
after the server has started running.
##Compile and Run Instructions
To start the server:
 Go into the Servers directory
 Start sbt
 compile
 run
To start the client in another Terminal window:
 Go to the client folder
 sbt
 compile
 run
 chose the map reduce functionality you would like
It should print the results of the map and reduce functions to the terminal.
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
The client application gives the user the choices of: word count, reverse index, and incoming
link count. The word count and reverse index functions use code from the lecture and
homework respectively. The incoming link count function utilizes code from the web crawler
assignment. Given a starting url, it gathers all links on all pages for the set of html pages
within a host. This is done during the map phase, and the reduce phase aggregates all of
the links for each page. The total for each page is the number of incoming links that the
page has within that set of pages.
###Generality of Framework
The generality of the framework is represented by the dierent types of tasks it can manage.
While these tasks are dierent they are not dissimilar. For the most part it is a matter of
parsing and counting data, then summing it. The dierences are slight in nature.
###Scalability 
To increase scalability the use of remote workers was employed. A round-robin router
generated multiple workers for each task.
##Issues/TODO
The biggest issue by far is determining how to get the functions to the remote workers.
As well as getting all of the relevant information to each one. After getting the architecture
in place,which is admittedly convoluted, I had trouble with the actual functionality of the
methods themselves. A fair amount of the code needs tweaking, but I wanted to show the
intent of the code and design even if it was not fully working. 

