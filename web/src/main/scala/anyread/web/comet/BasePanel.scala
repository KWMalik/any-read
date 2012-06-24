package anyread.web.comet

import net.liftweb.http.{PartialUpdateMsg, CometActor}
import anyread.web.single.SinglePageState
import net.liftweb.http.js.JsCmds.{Run, SetHtml}
import anyread.web.widgets.Widget
import anyread.web.states.PageState

/**
 * @author anton.safonov
 */

trait BasePanel extends CometActor {
  override def lowPriority = {
    case r: Redraw => redraw(r.state)
  }

  def redraw(state: PageState) {
    this ! PartialUpdateMsg(() => {
      SinglePageState.set(Some(state))
      SetHtml(uniqueId, panelWidget.draw()) &
      Run("rewriteUrl('%s', '/'+'%s')".format(state, state.buildUrl()))
    })
  }

  def panelWidget: Widget

  def render = "*" #> panelWidget.draw()

  override protected def localSetup() {
    PanelRegister ! AddPanel(this)
  }

  override protected def localShutdown() {
    PanelRegister ! RemovePanel(this)
  }
}
