package anyread.web.states

import net.liftweb.http.{RewriteResponse, ParsePath, RewriteRequest}
import net.liftweb.util.NamedPF
import anyread.web.single.SinglePageState

trait PageState {
  def buildUrl(): String
}

trait PageStateHandler {
  def path: List[String]

  def rewrite: Option[PartialFunction[RewriteRequest, RewriteResponse]] = None
}

case class GreenNameState(name: String) extends PageState {

  def buildUrl() = (GreenNameStateHandler.path ::: name :: Nil).mkString("/")
}

case object GreenNameStateHandler extends PageStateHandler {
  val path = "names" :: "green" :: Nil

  override def rewrite: Option[PartialFunction[RewriteRequest, RewriteResponse]] = {
    Some(
      NamedPF("greenName") {
        case RewriteRequest(ParsePath("names" :: "green" :: name :: Nil, _, _, _), _, _) => {
          SinglePageState(Some(new GreenNameState(name)))
          RewriteResponse(ParsePath("index" :: Nil, "", true, false), Map(), true)
        }
      }
    )
  }
}

case object RedNameStateHandler extends PageStateHandler {
  val path = "names" :: "red" :: Nil

  override def rewrite: Option[PartialFunction[RewriteRequest, RewriteResponse]] = {
    Some(
      NamedPF("redName") {
        case RewriteRequest(ParsePath("names" :: "red" :: Nil, _, _, _), _, _) => {
          SinglePageState(Some(RedNameState))
          RewriteResponse(ParsePath("index" :: Nil, "", true, false), Map(), true)
        }
      }
    )
  }
}

case object RedNameState extends PageState {
  def buildUrl() = RedNameStateHandler.path.mkString("/")
}