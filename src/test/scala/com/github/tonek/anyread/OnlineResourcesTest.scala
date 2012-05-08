package com.github.tonek.anyread

import org.scalatest.FunSuite
import org.jsoup.Jsoup
import java.io.{File, FileWriter}

/**
 * @author anton.safonov
 */

class OnlineResourcesTest extends FunSuite with Logging with NodesUtil {
  test("habr") {
    val file = new File("habtest.html")
    println("goto: file://localhost" + file.getAbsolutePath)
    val url = "http://www.upbeat.it/2012/05/08/on-why-i-am-not-buying-rubymotion/"
    debugPrettyHeader("Processing url " + url)
    val document = Jsoup.connect(url).get()
    val p = new Prettify(JsoupNodes)
    val content = p.getContent(new JsoupElementWrapper(document))

    val writer = new FileWriter(file)
    writer.write(content.toPrintableString())
    writer.close()
    print(content.toPrintableString())

  }
}
