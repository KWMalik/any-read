package anyread.web.widgets

import anyread.feed.RssFeedLoader
import xml.Unparsed

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
          ".rss-item-date *" #> format(feed.date)
      }
    )
    css(hiddenT("rss", "rss_list"))
  }
}
