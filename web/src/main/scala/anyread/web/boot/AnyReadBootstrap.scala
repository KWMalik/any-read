package anyread.web.boot

import net.liftweb.http.Bootable

/**
 * @author anton.safonov
 */

class AnyReadBootstrap extends Bootable {
  def boot() {
    new Boot().boot()
  }
}
