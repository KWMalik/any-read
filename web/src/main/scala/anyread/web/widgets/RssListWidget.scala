package anyread.web.widgets

import anyread.feed.RssFeedLoader
import xml.Unparsed
import net.liftweb.http.SHtml
import anyread.web.snippet.MainPage
import anyread.web.states.PreviewPageState

/**
 * @author anton.safonov
 */

object RssListWidget extends Widget {
  def draw() = {
    val css = ".rss-item *" #> RssFeedLoader.load().map(
      feed => {
        ".rss-item-link [href]" #> feed.link &
          ".rss-item-link *" #> feed.name &
          ".rss-item-extract *" #> Unparsed(feed.extract) &
          ".rss-item-date *" #> format(feed.date) &
          ".rss-item-preview [onclick]" #> SHtml.ajaxInvoke(
            () => MainPage.redrawAndRewrite(new PreviewPageState(feed.id))
          )
      }
    )
    css(hiddenT("rss", "rss_list"))
  }
}
