package anyread.web.widgets

import anyread.web.single.SinglePageState
import anyread.web.states.PreviewPageState
import anyread.feed.FeedDao
import anyread.web.preview.Preview

/**
 * @author anton.safonov
 */

object PreviewWidget extends Widget {
  def draw() = {
    val state = SinglePageState.get.asInstanceOf[PreviewPageState]
    val feed = FeedDao.byId(state.id)
    val preview = Preview(feed.link)
    val css = ".preview-content *" #> preview
    css(hiddenT("rss", "preview"))
  }
}
