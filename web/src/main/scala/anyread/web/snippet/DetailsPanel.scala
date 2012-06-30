package anyread.web.snippet

import anyread.web.widgets.{LightPreviewWidget, EmptyWidget, PreviewWidget}
import anyread.web.single.SinglePageState
import anyread.web.states.PreviewPageState

/**
 * @author anton.safonov
 */

object DetailsPanel extends BasePanel {
  protected def panelWidget = SinglePageState.get match {
    case PreviewPageState(_, true) => PreviewWidget
    case PreviewPageState(_, false) => LightPreviewWidget
    case _ => EmptyWidget
  }
}
