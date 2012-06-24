package anyread.web.comet

import net.liftweb.actor.LiftActor

/**
 * @author anton.safonov
 */

object PanelRegister extends LiftActor {
  private var panels: Set[LiftActor] = Set()

  protected def messageHandler = {
    case AddPanel(panel) => {
      panels += panel
    }
    case RemovePanel(panel) => panels -= panel
    case r : Redraw => {
      panels.foreach(_ ! r)
    }
  }
}
