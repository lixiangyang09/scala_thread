package lxyTest

import java.io.FileWriter
import java.util.concurrent._


object Example {
  val numberThread = 10
  val blockQueue = new ArrayBlockingQueue[(Int,String)](numberThread)
  val writer = new FileWriter("test")
  val result = new Array[(Int, String)](numberThread)
  val action = new Runnable {
    override def run() = {
      for (i <- 0 until numberThread){
        writer.write(result(i).toString())
        result(i) = (0, "*")
      }
    }
  }
  val barrier = new CyclicBarrier(numberThread, action)
  
  def main(args: Array[String]): Unit = {
    println("start producer")
    val producer = new Producer(blockQueue,numberThread)
    producer.start()

    println("start consumers")
//    val consumers = for (i <- 0 until numberThread) yield {
//      new Consumer(blockQueue, barrier,result)
//    }
//    consumers.foreach(_.start())
//    consumers.foreach(_.join())
    val executor = Executors.newFixedThreadPool(numberThread)
    for (i <- 0 until numberThread) executor.execute(new Consumer(blockQueue, barrier,result))
    println("join")
    producer.join()

    executor.shutdown()
    executor.awaitTermination(Long.MaxValue,TimeUnit.NANOSECONDS)

    result.foreach(s => {
      if (s != (0,"*")) writer.write(s.toString())
    })
    println("all finished")
    writer.close()
  }
}