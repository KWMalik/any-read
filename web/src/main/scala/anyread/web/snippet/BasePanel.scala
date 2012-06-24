package anyread.web.snippet

import anyread.web.widgets.Widget
import xml.NodeSeq
import net.liftweb.util.Helpers._

/**
 * @author anton.safonov
 */

trait BasePanel {
  self =>

  val uniqueId: String = "PanelId_" + self.getClass.getCanonicalName
    .substring(0, self.getClass.getCanonicalName.size-1).replaceAll("\\.", "_")

//  def redraw(): JsCmd = {
//      SetHtml(uniqueId, panelWidget.draw())
//        Run("rewriteUrl('%s', '/'+'%s')".format(state, state.buildUrl()))
//  }

  def draw(): NodeSeq = panelWidget.draw()

  protected def panelWidget: Widget

  def render(ns: NodeSeq) = {
    val fixedNs = <div id={uniqueId}>{ns}</div>

    val css = "#%s *".format(uniqueId) #> draw()
    css(fixedNs)
  }
}
