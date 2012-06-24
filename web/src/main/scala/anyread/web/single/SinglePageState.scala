package anyread.web.single

import net.liftweb.http.RequestVar
import anyread.web.states.{RssListState, RedNameState, PageState}

/**
 * @author anton.safonov
 */

object SinglePageState extends RequestVar[PageState](RssListState)
