package anyread.web.single

import anyread.web.states.PageStateHandler
import anyread.web.snippet.BasePanel

/**
 * @author anton.safonov
 */

object PanelsRegistry {
  private val panels: Map[PageStateHandler, Set[BasePanel]] = Map()
}
