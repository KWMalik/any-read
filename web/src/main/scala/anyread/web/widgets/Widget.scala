package anyread.web.widgets

import xml.NodeSeq

/**
 * @author anton.safonov
 */

trait Widget {
  def draw(): NodeSeq
}
