package anyread.web.single

import net.liftweb.http.RequestVar
import anyread.web.states.{RedNameState, PageState}

/**
 * @author anton.safonov
 */

object SinglePageState extends RequestVar[Option[PageState]](Some(RedNameState))
