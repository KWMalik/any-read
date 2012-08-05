package anyread.web.widgets

import net.liftweb.util.Props
import net.liftweb.http.S

/**
 * @author anton.safonov
 */

object GoogleOauthButton extends Widget {
  def draw() = {
    val css = ".oauth-button-ref [href]" #>
      ("https://accounts.google.com/o/oauth2/auth?" +
        "scope=" +
        "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+" +
        "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+" +
        S.encodeURL("http://www.google.com/reader/api/0/+") +
        S.encodeURL("http://www.google.com/reader/atom/") +
        "&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fauth%2Foauthcallback%2Fgoogle" +
        "&response_type=code" +
        "&client_id=" + Props.get("oauth.google.client_id").open_!)

    css(hiddenT("auth", "gauth"))
  }
}
