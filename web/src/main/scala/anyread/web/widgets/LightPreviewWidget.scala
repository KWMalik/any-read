package anyread.web.widgets

import anyread.web.single.SinglePageState
import anyread.web.states.PreviewPageState
import anyread.feed.FeedDao
import xml.Unparsed
import net.liftweb.http.SHtml
import anyread.web.snippet.{DetailsPanel, MainPage}

/**
 * @author anton.safonov
 */

object LightPreviewWidget extends Widget {
  def draw() = {
    val state = SinglePageState.get.asInstanceOf[PreviewPageState]
    val feed = FeedDao.byId(state.id)
    val css = ".light-preview-content *" #> Unparsed(feed.extract) &
      ".show-full-preview [onclick]" #> SHtml.ajaxInvoke(
        () => MainPage.redrawAndRewrite(new PreviewPageState(feed.id, true), state, DetailsPanel)
      )
    css(hiddenT("rss", "light_preview"))
  }
}
