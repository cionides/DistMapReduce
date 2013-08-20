import com.typesafe.config.ConfigFactory

import collection.JavaConversions._

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaSet
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.io.Source
import scala.util.matching.Regex

import java.net.URL
import java.io.File
import java.util.ArrayList
import java.util.StringTokenizer
import java.util.HashMap


import akka.actor._
import akka.routing.RoundRobinRouter


case class map1 extends Actor{
def receive = { 
case _ => System.out.println("Test")
}

}
object MapReduceApplication {

  def main(args: Array[String]) {
  	val message = "Please enter the type of map reduce job you would like to perform: " + "\n" + "1. Word Count" + "\n" + "2. Reverse Index" + "\n" + "3. Incoming Link Count" + "\n" + "4. Quit"
   	System.out.println(message)
   	val jobType = Console.readInt()
    
    val system = ActorSystem("RemoteServer",ConfigFactory.load.getConfig("remotelookup"))
    val master = system.actorFor("akka.tcp://RemoteServer@127.0.0.1:2552/user/master")

   	jobType match{
   		case 1 => {  		  		
   			System.out.println("Word Count")
   			//send the word count map and reduce functions to the remote workers to be compiled and called
   		     master !("case1")
   			}
   		case 2 => {
   			System.out.println("Reverse Index")
   			//send the reverse index map and reduce functions to the remote workers to be compiled and called
   			
   			 master ! ("map2", "red2")
   			}
   		case 3 => {
   			System.out.println("Incoming Link Count")
   			//send the Incoming Link Count map and reduce functions to the remote workers to be compiled and called
   			
   			 master ! ("map3", "red3")
   			}
   		case 4 => {
   			System.out.println("Goodbye!")
   			
   			}  			
   	}
}
}

