package anyread.web.widgets

import xml.NodeSeq
import anyread.web.single.SinglePageState
import anyread.web.states.GreenNameState

/**
 * @author anton.safonov
 */

object GreenNameWidget extends Widget {
  def draw(): NodeSeq = {
    val state = SinglePageState.get.get.asInstanceOf[GreenNameState]
    val css = ".name *" #> state.name & ".name-wrapper [class+]" #> "green"
    css(hiddenT("name_widget"))
  }
}
