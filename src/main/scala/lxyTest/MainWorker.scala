package lxyTest

import java.io.FileWriter
import java.util.concurrent._

/**
  * Created by lxy on 2017/2/22.
  */
class MainWorker(users:ArrayBlockingQueue[String], numberThread:Int) extends  Thread{
  val blockQueue = new ArrayBlockingQueue[(Int,String)](numberThread)
  var writer :FileWriter = _
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

  override def run(): Unit = {
    println("start main worker")
    try{
      var exit = false
      while (!exit){
        val usr = users.take()
        if (usr != "noUser"){
          writer = new FileWriter(usr)
          println(s"starting work for $usr")
          println("start producer")
          val producer = new Producer(blockQueue,numberThread)
          producer.start()

          println("start consumers")
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
        } else {
          exit = true
        }
      }

    } catch {
      case e:BrokenBarrierException => println("thread barrier timeout")
      case  e:TimeoutException => println("the first time out thread")
    }

  }
}
