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

  def allHandlers = handlers

  def register(handler : PageStateHandler) {
    handlers += handler
  }
}

trait PageStateHandler {
  self =>
  StateHandlersRegistry.register(self)
  def path: List[String]

  def rewrite: Option[PartialFunction[RewriteRequest, RewriteResponse]] = None

  def deserialize : PartialFunction[(String, String), PageState]
}

case class GreenNameState(name: String) extends PageState {

  def buildUrl() = (GreenNameStateHandler.path ::: name :: Nil).mkString("/")

  val handler = GreenNameStateHandler

  val typeName = "GreenNameState"
}

case object GreenNameStateHandler extends PageStateHandler {
  val name = "GreenNameState"
  val path = "names" :: "green" :: Nil

  override def rewrite: Option[PartialFunction[RewriteRequest, RewriteResponse]] = {
    Some(
      NamedPF("greenName") {
        case RewriteRequest(ParsePath("names" :: "green" :: name :: Nil, _, _, _), _, _) => {
          SinglePageState(new GreenNameState(name))
          RewriteResponse(ParsePath("index" :: Nil, "", true, false), Map(), true)
        }
      }
    )
  }

  def deserialize = {
    case ("GreenNameState", state) => {
      implicit val formats = DefaultFormats
      val json = JsonParser.parse(state)
      json.extract[GreenNameState]
    }
  }
}

case object RedNameStateHandler extends PageStateHandler {
  val path = "names" :: "red" :: Nil

  override def rewrite: Option[PartialFunction[RewriteRequest, RewriteResponse]] = {
    Some(
      NamedPF("redName") {
        case RewriteRequest(ParsePath("names" :: "red" :: Nil, _, _, _), _, _) => {
          SinglePageState(RedNameState)
          RewriteResponse(ParsePath("index" :: Nil, "", true, false), Map(), true)
        }
      }
    )
  }

  def deserialize = {
    case ("RedNameState", state) => RedNameState
  }
}

case object RedNameState extends PageState {
  def buildUrl() = RedNameStateHandler.path.mkString("/")

  val handler = RedNameStateHandler

  val typeName = "RedNameState"
}