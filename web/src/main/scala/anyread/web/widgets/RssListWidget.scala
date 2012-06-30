package anyread.web.widgets

import anyread.feed.RssFeedLoader
import xml.Unparsed
import net.liftweb.http.SHtml
import anyread.web.snippet.{DetailsPanel, MainPage}
import anyread.web.states.PreviewPageState
import anyread.web.single.SinglePageState

/**
 * @author anton.safonov
 */

object RssListWidget extends Widget {
  def draw() = {
    val state = SinglePageState.get
    val css = ".rss-item *" #> RssFeedLoader.load().map(
      feed => {
        ".rss-item-link [href]" #> feed.link &
          ".rss-item-link *" #> feed.name &
          ".rss-item-date *" #> format(feed.date) &
          ".rss-item-preview [onclick]" #> SHtml.ajaxInvoke(
            () => MainPage.redrawAndRewrite(new PreviewPageState(feed.id), state, DetailsPanel)
          ) &
          ".rss-item-preview-full [onclick]" #> SHtml.ajaxInvoke(
            () => MainPage.redrawAndRewrite(new PreviewPageState(feed.id, true), state, DetailsPanel)
          )
      }
    )
    css(hiddenT("rss", "rss_list"))
  }
}
