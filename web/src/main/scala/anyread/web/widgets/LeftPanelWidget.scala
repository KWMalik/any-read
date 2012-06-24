package anyread.web.widgets

import anyread.web.single.SinglePageState
import anyread.web.states.{GreenNameState, RedNameState}

/**
 * @author anton.safonov
 */

object LeftPanelWidget extends Widget{
  def draw() = {
    val css = ".content" #> widget.draw()
    css(hiddenT("left_panel"))
  }

  private def widget: Widget = SinglePageState.get match {
    case RedNameState => RedNameWidget
    case GreenNameState(_) => GreenNameWidget
    case _ => throw new IllegalStateException("No state defined")
  }
}
