package anyread.web.comet

import net.liftweb.common.Full
import anyread.web.widgets.LeftPanelWidget

/**
 * @author anton.safonov
 */

class LeftPanel extends BasePanel {
  override def defaultPrefix = Full("leftPanel")

  def panelWidget = LeftPanelWidget
}