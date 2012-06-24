package anyread.feed

import java.net.URL
import org.joda.time.DateTime

/**
 * @author anton.safonov
 */

case class RssFeed(id: Long, link: String, name: String, extract: String, date: DateTime)
