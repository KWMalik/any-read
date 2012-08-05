package anyread.web.boot

import net.liftweb._
import common._
import http._
import js.JsCmds.Run
import java.util.Locale
import sitemap.Loc.Hidden
import sitemap.{*, **, SiteMap, Menu}
import util._
import Helpers._
import java.lang.reflect.InvocationTargetException
import anyread.web.states.StateHandlersRegistry
import anyread.web.snippet.MainPage
import anyread.web.auth.OauthAuthorizer

class Boot {

  def boot() {
    LiftRules.ajaxDefaultFailure = Empty
    LiftRules.ajaxRetryCount = Full(0)
    val russianLocale = new Locale("ru", "RU")
    Locale.setDefault(russianLocale)
    LiftRules.localeCalculator = _ => russianLocale

    Logger.setup = Full(Log4j.withFile(getClass.getResource("/log4j.properties")))

    // where to search snippet
    LiftRules.addToPackages("anyread.web")

    buildSiteMap()

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart = Full(
      () =>
        LiftRules.jsArtifacts.show("ajax-loader").cmd &
          Run("$('.ajax-submit').attr('disabled','true')")
    )

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd = Full(
      () =>
        LiftRules.jsArtifacts.hide("ajax-loader").cmd &
          Run("$('.ajax-submit').removeAttr('disabled')")
    )
    LiftRules.noticesAutoFadeOut.default.set(
      (notices: NoticeType.Value) => Full(5 seconds, 1 seconds)
    )

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.ajaxPostTimeout = 30000

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })

    LiftRules.exceptionHandler.prepend {
      case (_, r, exception) if exception.isInstanceOf[InvocationTargetException] => {
        val e = exception.asInstanceOf[InvocationTargetException]
        val target = e.getTargetException
        if(target != null && target.getMessage != null && target.getMessage.contains("not found")) {
          RedirectResponse("/404")
        } else {
          RedirectResponse("/error")
        }
      }
    }

    LiftRules.dispatch.append(MainPage.Rest)
    LiftRules.dispatch.append(OauthAuthorizer)

    rewriteRequests()
  }

  def rewriteRequests() {
    val rewrites = StateHandlersRegistry.allHandlers.filter(_.rewrite.isDefined).map(_.rewrite)
    LiftRules.statefulRewrite.append(
      NamedPF("AnyReadRequestRewriter") {
        rewrites.map(_.get).reduceLeft(_ orElse _)
      }
    )
  }

  def buildSiteMap() {

    val entries = List(
      Menu.i("Reader") / "index",
      Menu.i("OAuth") / "auth" / "oauthcallback" / ** >> Hidden
    )

    LiftRules.setSiteMap(SiteMap(entries: _*))
  }
}