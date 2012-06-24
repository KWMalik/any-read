package anyread.web.comet

import net.liftweb.actor.LiftActor

/**
 * @author anton.safonov
 */

trait PanelAction

case class AddPanel(panel: LiftActor)
case class RemovePanel(panel: LiftActor)
