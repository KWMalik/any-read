package anyread.web.snippet

import net.liftweb.http._
import collection.mutable
import anyread.web.states._
import net.liftweb.util.CssSel
import net.liftweb.http.js.JsCmds.Script
import net.liftweb.http.js.JsCmd
import anyread.web.single.SinglePageState
import net.liftweb.json.{Serialization, DefaultFormats}
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.util.Helpers._
import rest.RestHelper
import scala.Some

/**
 * @author anton.safonov
 */

object MainPage extends DispatchSnippet {

  private val panels: mutable.MultiMap[PageStateHandler, BasePanel]
  = new mutable.HashMap[PageStateHandler, mutable.Set[BasePanel]] with mutable.MultiMap[PageStateHandler, BasePanel]

  panels.addBinding(RssListState, MainContent)
  panels.addBinding(PreviewPageStateHandler, MainContent)
  panels.addBinding(PreviewPageStateHandler, DetailsPanel)


  def dispatch = {
    case "render" => render
  }

  def render: CssSel = {
      "#initScript" #> Script(initBackForward)
  }

  def redrawAndRewrite(state: PageState): JsCmd = redraw(state) & rewriteUrl()
  def redrawAndRewrite(state: PageState, prevState: PageState, toRedraw: BasePanel*): JsCmd = {
    partialRedraw(state, prevState, toRedraw:_*) & addToNavHistory(state)
  }

  def partialRedraw(state: PageState, prevState: PageState, toRedraw: BasePanel*): JsCmd = {
    SinglePageState.set(state)
    val cmds: Seq[JsCmd] = toRedraw.map(panel => SetHtml(panel.uniqueId, panel.draw()))
    cmds.reduceLeft(_ & _)
  }

  def redraw(state: PageState): JsCmd = {
    SinglePageState.set(state)
    val cmds: mutable.Set[JsCmd] = panels.get(state.handler) match {
      case Some(handlers) => handlers.map(panel => SetHtml(panel.uniqueId, panel.draw()))
      case _ => mutable.Set()
    }
    cmds.reduceLeft(_ & _)
  }

  private def rewriteUrl(): JsCmd = Run("rewriteUrl(%s)".format(buildRewriteParams()))
  private def addToNavHistory(state: PageState): JsCmd = Run("addHistory(%s)".format(buildRewriteParams()))

  private def buildRewriteParams(): String = buildRewriteParams(SinglePageState.get)

  private def buildRewriteParams(state: PageState): String = {
    implicit val formats = DefaultFormats
    val serializedState = Serialization.write(state)
    "'%s', '%s', '%s', '/'+'%s'".format(state.typeName, serializedState, state.typeName, state.url)
  }

  private def drawHistory(): LiftResponse = {
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

  object Rest extends RestHelper {
    serve {
      case "frmw" :: "navigate" :: Nil JsonGet _ => drawHistory()
    }
  }

  private lazy val initBackForward: JsCmd = {
    Run("initBackForward('%s', %s)".format("/frmw/navigate", buildRewriteParams()))
  }
}
