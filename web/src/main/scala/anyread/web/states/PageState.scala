package anyread.web.states

import net.liftweb.http.{RewriteResponse, ParsePath, RewriteRequest}
import net.liftweb.util.NamedPF
import anyread.web.single.SinglePageState
import net.liftweb.json.{DefaultFormats, JsonParser}

trait PageState {
  def buildUrl(): String

  def handler: PageStateHandler

  def typeName: String
}

object StateHandlersRegistry {
  private var handlers: Set[PageStateHandler] = Set()
  register(PreviewPageStateHandler)
  register(RssListState)

  def allHandlers = handlers

  def register(handler: PageStateHandler) {
    handlers += handler
  }
}

trait PageStateHandler {

  def path: List[String]

  def rewrite: Option[PartialFunction[RewriteRequest, RewriteResponse]] = None

  def deserialize: PartialFunction[(String, String), PageState]
}

case object RssListState extends PageState with PageStateHandler {
  val path = "rss" :: "list" :: Nil

  def buildUrl() = path.mkString("/")

  val handler = this

  val typeName = "RssList"

  def deserialize = {
    case ("RssList", state) => this
  }

  override def rewrite: Option[PartialFunction[RewriteRequest, RewriteResponse]] = {
    Some(
      NamedPF("preview") {
        case RewriteRequest(ParsePath("rss" :: "list" :: Nil, _, _, _), _, _) => {
          SinglePageState(this)
          RewriteResponse(ParsePath("index" :: Nil, "", true, false), Map(), true)
        }
      }
    )
  }
}

case class PreviewPageState(id: Long) extends PageState {
  def buildUrl() = (PreviewPageStateHandler.path ::: id.toString :: Nil).mkString("/")
  val handler = PreviewPageStateHandler
  val typeName = "PreviewPage"
}
object PreviewPageStateHandler extends PageStateHandler {
  def path = "preview" :: Nil

  override def rewrite: Option[PartialFunction[RewriteRequest, RewriteResponse]] = {
    Some(
      NamedPF("preview") {
        case RewriteRequest(ParsePath("preview" ::  id :: Nil, _, _, _), _, _) => {
          SinglePageState(new PreviewPageState(id.toLong))
          RewriteResponse(ParsePath("index" :: Nil, "", true, false), Map(), true)
        }
      }
    )
  }

  def deserialize = {
    case ("PreviewPage", state) => {
      implicit val formats = DefaultFormats
      val json = JsonParser.parse(state)
      json.extract[PreviewPageState]
    }
  }
}
