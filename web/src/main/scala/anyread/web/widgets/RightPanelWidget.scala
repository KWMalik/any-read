package anyread.web.widgets

import anyread.web.single.SinglePageState
import anyread.web.states.{GreenNameState, RedNameState}

/**
 * @author anton.safonov
 */

object RightPanelWidget extends Widget{
  def draw() = {
    val css = ".content1" #> widget.draw() & ".content2" #> widget.draw()
    css(hiddenT("right_panel"))
  }

  private def widget: Widget = SinglePageState.get match {
    case Some(RedNameState) => RedNameWidget
    case Some(GreenNameState(_)) => GreenNameWidget
    case _ => throw new IllegalStateException("No state defined")
  }
}
