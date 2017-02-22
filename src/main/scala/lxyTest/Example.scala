package lxyTest

import java.io.FileWriter
import java.util.concurrent._


object Example {

  val concorrent = 3
  val userList = new ArrayBlockingQueue[String](concorrent)

  def main(args: Array[String]): Unit = {
    val users = Array("abc", "lxy", "dbg", "xiaoming")
    val mainWorkers = Executors.newFixedThreadPool(concorrent)
    for (i <- 0 until concorrent) mainWorkers.execute(new MainWorker(userList,concorrent))


    users.foreach(usr => {
      userList.put(usr)
      println(s"put user in to userList: $usr")
    })

    for (i <-0 until concorrent) {
      userList.put("noUser")
      println(s"put user in to userList: noUser")
    }


    mainWorkers.shutdown()
    mainWorkers.awaitTermination(Long.MaxValue, TimeUnit.NANOSECONDS)
    println("all done")
  }
}