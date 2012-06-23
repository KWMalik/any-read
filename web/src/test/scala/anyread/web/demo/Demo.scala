package anyread.web.demo

import org.eclipse.jetty.server.Server
import scala.Array
import tools.nsc.io.Path
import java.io.FileNotFoundException
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.webapp.WebAppContext

/**
 * @author anton.safonov
 */

object Demo {
  def main(args: Array[String])
  {
    System.setProperty("run.mode", "development")
    val server = new Server
    val scc = new SelectChannelConnector
    scc.setPort(8080)
    server.setConnectors(Array(scc))

    val context = new WebAppContext()
    context.setServer(server)
    context.setContextPath("/")

    val dirsToCheck = List("../src/main/webapp", "web/src/main/webapp")
    val path = dirsToCheck.find(Path(_).exists).getOrElse(throw new FileNotFoundException("Path to webapp not found"))

    context.setWar(path)

    server.setHandler(context)

    try {
      println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP")
      server.start()
      while (System.in.available() == 0) {
        Thread.sleep(5000)
      }
      server.stop()
      server.join()
    } catch {
      case exc: Exception => {
        exc.printStackTrace()
        System.exit(100)
      }
    }
  }
}
