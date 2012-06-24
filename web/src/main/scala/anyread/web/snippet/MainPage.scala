package anyread.web.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml
import anyread.web.comet.{Redraw, PanelRegister}
import net.liftweb.http.js.JsCmds
import anyread.web.states.{RedNameState, GreenNameState}

/**
 * @author anton.safonov
 */

class MainPage {
  def render = {
    ".to-green [onclick]" #> SHtml.ajaxInvoke(() => {
      PanelRegister ! new Redraw(new GreenNameState("bla-bla"))
      JsCmds.Noop
    }) &
      ".to-red [onclick]" #> SHtml.ajaxInvoke(() => {
        PanelRegister ! new Redraw(RedNameState)
        JsCmds.Noop
      })
  }
}
