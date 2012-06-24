package anyread.web.comet

import net.liftweb.common.Full
import anyread.web.widgets.RightPanelWidget

/**
 * @author anton.safonov
 */

class RightPanel extends BasePanel{
  override def defaultPrefix = Full("rightPanel")

  def panelWidget = RightPanelWidget

}
