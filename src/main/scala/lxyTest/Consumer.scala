package lxyTest

import java.util.concurrent._

/**
  * Created by lxy on 2017/2/22.
  */
class Consumer(queue:ArrayBlockingQueue[(Int,String)], barrier:CyclicBarrier,res:Array[(Int, String)]) extends Thread{

  override def run():Unit = {
    try{
      var exit = false
      while (!exit){
        //Thread.sleep(500)
        val data = queue.take()
        if (data == (0, "*")) {
          exit = true
        } else {
          res(data._1) = data
          barrier.await(1, TimeUnit.SECONDS)
          println(s"consume: $data")
        }
      }
      println("exit run method")
    } catch {
      case e:BrokenBarrierException => println("thread barrier timeout")
      case  e:TimeoutException => println("the first time out thread")
    }

  }
}
