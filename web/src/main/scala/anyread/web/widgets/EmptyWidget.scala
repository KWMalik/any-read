package anyread.web.widgets

import xml.NodeSeq

/**
 * @author anton.safonov
 */

object EmptyWidget extends Widget{
  def draw() = NodeSeq.Empty
}
