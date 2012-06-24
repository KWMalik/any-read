package anyread.web.snippet

import anyread.web.widgets.{EmptyWidget, PreviewWidget}
import anyread.web.single.SinglePageState
import anyread.web.states.PreviewPageState

/**
 * @author anton.safonov
 */

object DetailsPanel extends BasePanel {
  protected def panelWidget = SinglePageState.get match {
    case PreviewPageState(_) => PreviewWidget
    case _ => EmptyWidget
  }
}
