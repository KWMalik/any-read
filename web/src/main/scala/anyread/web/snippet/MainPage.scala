package anyread.web.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.{DispatchSnippet, SHtml}
import net.liftweb.http.js.JsCmd
import anyread.web.states._
import net.liftweb.util.CssSel
import anyread.web.single.SinglePageState
import collection.mutable
import anyread.web.states.GreenNameState
import net.liftweb.http.js.JsCmds.{SetHtml, Run}

/**
 * @author anton.safonov
 */

object MainPage extends DispatchSnippet{

  private val panels: mutable.MultiMap[PageStateHandler, BasePanel]
  = new mutable.HashMap[PageStateHandler, mutable.Set[BasePanel]] with mutable.MultiMap[PageStateHandler, BasePanel]

  panels.addBinding(GreenNameStateHandler, LeftPanel)
  panels.addBinding(GreenNameStateHandler, RightPanel)
  panels.addBinding(RedNameStateHandler, LeftPanel)
  panels.addBinding(RedNameStateHandler, RightPanel)


  def dispatch = {
    case "render" => render
  }

  def render: CssSel = {
    ".to-green [onclick]" #> SHtml.ajaxInvoke(() => redraw(new GreenNameState("bla-bla"))) &
      ".to-red [onclick]" #> SHtml.ajaxInvoke(() => redraw(RedNameState))
  }

  def redraw(state: PageState): JsCmd = {
    SinglePageState.set(state)
    val cmds: mutable.Set[JsCmd] = panels.get(state.handler) match {
      case Some(handlers) => handlers.map(panel => SetHtml(panel.uniqueId, panel.draw()))
      case _ => mutable.Set()
    }
    cmds.reduceLeft(_ & _) & rewriteUrl()
  }

  private def rewriteUrl(): JsCmd = {
    val state = SinglePageState.get
    Run("rewriteUrl('%s', '/'+'%s')".format(state, state.buildUrl()))
  }
}
