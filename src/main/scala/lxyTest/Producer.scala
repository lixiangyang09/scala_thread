package lxyTest

import java.util.concurrent.ArrayBlockingQueue

import scala.util.Random

/**
  * Created by lxy on 2017/2/22.
  */
class Producer(queue:ArrayBlockingQueue[(Int,String)], numberThread:Int) extends Thread{
  val totData = 99
  val random = new Random()
  override def run():Unit = {
    for (i <- 0 until totData){
      val delay = random.nextInt(10)
     // Thread.sleep(delay*100)
      queue.put((i % numberThread,i.toString))
      println(s"produced: ${i}")
    }
    for (i <- 0 until numberThread){
      queue.put((0,"*"))
     println(s"produced: *")

    }
  }
}
