package anyread.web.states

import net.liftweb.http.{ParsePath, RewriteResponse, RewriteRequest}
import net.liftweb.util.NamedPF
import net.liftweb.json.{JsonParser, DefaultFormats}
import anyread.web.single.SinglePageState

/**
 * @author anton.safonov
 */

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
