package anyread.web.widgets

import xml.NodeSeq

/**
 * @author anton.safonov
 */

object RedNameWidget extends Widget{
  def draw(): NodeSeq = {
    val css = ".name *" #> "red" & ".name-wrapper [class+]" #> "red"
    css(hiddenT("name_widget"))
  }
}
