package anyread.web.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.js.JsCmd
import anyread.web.states._
import net.liftweb.util.CssSel
import anyread.web.single.SinglePageState
import collection.mutable
import js.JsCmds.{Script, SetHtml, Run}
import net.liftweb.http.S.SFuncHolder
import scala.Some
import anyread.web.states.GreenNameState
import net.liftweb.json.{DefaultFormats, Serialization}

/**
 * @author anton.safonov
 */

object MainPage2 extends DispatchSnippet{

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
    ".to-green [onclick]" #> SHtml.ajaxInvoke(() => redrawAndRewrite(new GreenNameState("bla-bla"))) &
      ".to-red [onclick]" #> SHtml.ajaxInvoke(() => redrawAndRewrite(RedNameState)) &
      "#initScript" #> Script(initBackForward)

  }

  def redrawAndRewrite(state: PageState): JsCmd = redraw(state) & rewriteUrl()

  def redraw(state: PageState): JsCmd = {
    SinglePageState.set(state)
    val cmds: mutable.Set[JsCmd] = panels.get(state.handler) match {
      case Some(handlers) => handlers.map(panel => SetHtml(panel.uniqueId, panel.draw()))
      case _ => mutable.Set()
    }
    cmds.reduceLeft(_ & _)
  }

  private def rewriteUrl(): JsCmd = {
    val state = SinglePageState.get
    implicit val formats = DefaultFormats
    val serializedState = Serialization.write(state)
    Run(
      "rewriteUrl('%s', '%s', '%s', '/'+'%s')"
        .format(state.typeName, serializedState, state.typeName, state.buildUrl())
    )
  }

  private def drawHistory(ignored: String): LiftResponse = {
    val newState = fetchNewState()
    val prevState = fetchPrevState()

    JavaScriptResponse(redraw(newState))
  }

  private lazy val stateExtractor = StateHandlersRegistry.allHandlers.map(_.deserialize).reduceLeft(_ orElse _)
  private def fetchNewState(): PageState = {
    stateExtractor.apply(S.param("newStateType").openOr("") -> S.param("newState").openOr(""))
  }

  private def fetchPrevState(): Option[PageState] = {
    stateExtractor.lift.apply(S.param("newStateType").openOr("") -> S.param("newState").openOr(""))
  }

  private lazy val initBackForward: JsCmd = {
    S.fmapFunc(SFuncHolder(drawHistory))(
      func => {
        val url = S.encodeURL(S.contextPath + "/" + LiftRules.ajaxPath) + "?" + func + "=_"
        Run("initBackForward('%s')".format(url))
      }
    )
  }
}
