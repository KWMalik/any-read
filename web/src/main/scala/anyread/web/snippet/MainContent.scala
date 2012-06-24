package anyread.web.snippet

import anyread.web.widgets.{PreviewWidget, RssListWidget}
import anyread.web.single.SinglePageState
import anyread.web.states.{PreviewPageState, RssListState}

/**
 * @author anton.safonov
 */

object MainContent extends BasePanel {
  protected def panelWidget = SinglePageState.get match {
    case RssListState => RssListWidget
    case PreviewPageState(_) => RssListWidget
    case unexpected => throw new IllegalStateException("Unexpected state " + unexpected)
  }
}
