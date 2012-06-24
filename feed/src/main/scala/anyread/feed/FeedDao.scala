package anyread.feed

import java.util.concurrent.ConcurrentHashMap
import org.joda.time.DateTime
import java.util.concurrent.atomic.AtomicLong
import scala.collection.JavaConversions._
import collection.mutable

/**
 * @author anton.safonov
 */

object FeedDao {
  private val ids: AtomicLong = new AtomicLong(0)
  private val byLink: mutable.ConcurrentMap[String, RssFeed] = new ConcurrentHashMap[String, RssFeed]()
  private val feeds: mutable.ConcurrentMap[Long, RssFeed] = new ConcurrentHashMap[Long, RssFeed]()

  def create(link: String, name: String, extract: String, date: DateTime = new DateTime()): RssFeed = {
    byLink.get(link) match {
      case None => {
        val feed = RssFeed(ids.incrementAndGet(), link, name, extract, date)
        byLink.put(feed.link, feed)
        feeds.put(feed.id, feed)
        feed
      }
      case Some(feed) => feed
    }
  }

  def byId(id: Long): RssFeed = feeds(id)
}
