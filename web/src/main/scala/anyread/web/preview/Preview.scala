package anyread.web.preview

import xml.{Unparsed, NodeSeq}
import org.jsoup.Jsoup
import com.github.tonek.anyread.{JsoupElementWrapper, JsoupNodes, Prettify}

/**
 * @author anton.safonov
 */

object Preview {
  def apply(url: String): NodeSeq = {
    val document = Jsoup.connect(url).get()
    val p = new Prettify(JsoupNodes)
    val content = p.getContent(new JsoupElementWrapper(document))
    Unparsed(content.toPrintableString())
  }
}
