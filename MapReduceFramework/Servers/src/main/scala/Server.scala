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

case class Word(val word:String,val count:Integer)
case class Words(val word:String,val title:String)
case class Result
case class MapData(val dataList: ArrayList[Word])
case class ReduceData(val reduceDataMap: java.util.HashMap[String, Integer])
case class Book(title: String, url: String)
class Done


class MasterActor extends Actor{
  
  val roundRobinRouter =context.actorOf(Props[MapActor].withRouter(RoundRobinRouter(5)), "router")
  
  def receive = {
    case funs: Tuple2[String, String] => {
      val mapType = funs._1
      val redType = funs._2
      //get the appropriate functions and send them to the remote workers using a router
      //val roundRobinRouter =context.actorOf(Props[new MapActor()].withRouter(RoundRobinRouter(5)), "router")
      }
    
    case s:String => {
      if(s.equals("case1")){
        router ! map1( _ : String)
    	router ! reduce1( _ : ArrayList[Word])
    	}
      if(s.equals("case2")){
    	router ! map2( _ : String)
    	router ! reduce1( _ : ArrayList[Word])
    	}
      if(s.equals("case3")){
    	router ! map3( _ : String)
    	router ! reduce1( _ : ArrayList[Word])
    	}
    }
    
    case finRes1: MapData => System.out.println(finRes1)
    
    case finRes2: ArrayList[Words] => System.out.println(finRes2)
	
    case finRes3: List[String] => System.out.println(finRes3)
    
    case _ => println("Received unknown msg ")
    }
   
//MAP FUNCTIONS
  def map1(line: String): MapData = {
    val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be", "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")
    System.out.println(line)
    var dataList = new ArrayList[Word]
    var parser: StringTokenizer = new StringTokenizer(line)
	var defaultCount: Integer = 1
	while (parser.hasMoreTokens()) {
	  var word: String = parser.nextToken().toLowerCase()
	  if (!STOP_WORDS_LIST.contains(word)) {
	    dataList.add(new Word(word, defaultCount))
	    }
	  }
    return new MapData(dataList)
    }
  
  def map2(b : Book):ArrayList[Words] = {
    val title = b.title
    val url = b.url
    val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be","do", "go", "if", "in", "is", "it", "of", "on", "the", "to")
    var wordContent = ArrayList[Words]
    val content = Source.fromURL(url).mkString
    
    for (word <- content.split("[\\p{Punct}\\s]+"))
      if ((!STOP_WORDS_LIST.contains(word)) && word(0).isUpper) {
        var index = Math.abs((word.hashCode())%jobs)
	    val w = Words(word, title)
	    wordContent.add(w)
      }
      return wordContent
  }
  
  def map3(url: String, host: String): List[String] = {	
    var visited = HashSet[String]()
    var URLMatcher = new Regex("href=\"(http://[^\"]+)\"", "url")
    val host = (new java.net.URL(url)).getHost()		
    val urlHost = (new java.net.URL(url)).getHost()
    if (urlHost == host){
      visited += url
      val pageLinkSet = (matcher findAllIn Source.fromURL(url).mkString matchData).map(_.group("url")).toList.removeDuplicates
      return pageLinkSet
      }
    }
  }

class MapActor extends Actor {
  val roundRobinRouter =context.actorOf(Props[ReduceActor].withRouter(RoundRobinRouter(5)), "router")
  val master = context.actorOf(Props[MasterActor])
  
  def receive ={
    
  case fun1: ((String) => (MapData))  => {
      val s = ("The quick brown fox tried to jump over the lazy dog and fell on the dog")
      router ! (fun1(s), reduce1( _ : ArrayList[Word]))
      }
  
  case fun2: ((Book) => (ArrayList[Words])) => {						
    val b = Book("Oliver Twist", "http://www.gutenberg.org/cache/epub/730/pg730.txt")
    router ! (fun2(b), reduce2( _ : Word))
    }
  
  case fun3: ((String, String) => (List[String])) => {
    val url = ("http://www.wikipedia.org")
    val host = (new java.net.URL(url)).getHost()  						
    router ! (fun3(url, host), reduce3( _ : List[String]))
    }
		
  case res1: MapData => master ! res1
		
  case res2: ArrayList[Words] => master ! res2
		
  case res3: List[String] => master ! res3
		
  case _ => System.out.println("Unknown")
	
  }

  //REDUCE FUNCTIONS
  def reduce1(dataList: ArrayList[Word]): java.util.HashMap[String, Integer] = {
    var finalReducedMap = new java.util.HashMap[String, Integer]
	var reducedMap = new java.util.HashMap[String, Integer]
    
    for (wc:Word <- dataList) {
      var word: String = wc.word
      
      if (reducedMap.containsKey(word)) {
        reducedMap.put(word,(reducedMap.get(word)+1) )
        } 
      else {
          reducedMap.put(word, 1)
          }
      }
    
    val rd = new ReduceData(reducedMap)
	val reducedList = rd.reduceDataMap
	var count: Integer = 0
	
	for (key <- reducedList.keySet) {
	  
	  if (finalReducedMap.containsKey(key)) {
	    count = reducedList.get(key)
	    count += finalReducedMap.get(key)
	    finalReducedMap.put(key, count)
	    } 
	  else {
	    finalReducedMap.put(key, reducedList.get(key))
	    }
	  }
    return finalReducedMap
    }
  
  def reduce2(words: Word): scala.collections.mutable.HashMap[String, List[String]]{
    
    val word = words.word
 	val title = words.title
  	var pending = 0
  	var reduceMap = new HashMap[String, List[String]]()
  	
  	if (reduceMap.contains(word)) {
  	  if (!reduceMap(word).contains(title))
  	    reduceMap += (word -> (title :: reduceMap(word)))
  	    }
  	else {
  	  reduceMap += (word -> List(title))
  	  }
    return reduceMap
    }
  
  def reduce3(dataList: List[String]): ReduceData = {
    var reducedMap = new java.util.HashMap[String, Integer]
    for (word:String <- dataList) {
      if (reducedMap.containsKey(word)) {
        reducedMap.put(word,reducedMap.get(word)+1 )
		} 
      else {
			reducedMap.put(word, 1)
		}
	}
    
    val rd = new ReduceData(reducedMap)
	val reducedList = rd.reduceDataMap
	var count: Integer = 0
	for (key <- reducedList.keySet) {
		if (finalReducedMap.containsKey(key)) {
			count = reducedList.get(key)
			count += finalReducedMap.get(key)
			finalReducedMap.put(key, count)
		} else {
			finalReducedMap.put(key, reducedList.get(key))
		}
	}
}
}

class ReduceActor extends Actor{
  
  def receive = {
    case fun1: Tuple2(MapData, ((ArrayList[Word]) => (ReduceData))) => {
      sender ! fun1_.2(fun1._2)
	}
	
    case fun2: Tuple2(MapData, ((ArrayList[Word]) => (ReduceData))) => {
      sender ! fun1_.2(fun1._2)
	}
	
    case fun3: Tuple2(MapData, ((ArrayList[Word]) => (ReduceData))) => {
	  sender ! fun1_.2(fun1._2)
	}
	
    case _ => System.out.println("Unknown")
	}
}

object Server extends App {
  
  val system = ActorSystem("RemoteServer", ConfigFactory.load.getConfig("RemoteSys"))
  val master = system.actorOf(Props[MasterActor], name="master")
  
  println("Server ready")
  
}
