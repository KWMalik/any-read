package anyread.web.snippet.topbar

import anyread.web.widgets.GoogleOauthButton
import net.liftweb.util.Helpers._

/**
 * @author anton.safonov
 */

object AuthButtons {
  def render = ".goauth" #> GoogleOauthButton.draw()
}
