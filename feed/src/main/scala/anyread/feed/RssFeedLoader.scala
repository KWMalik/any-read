package anyread.feed

import java.net.URL
import com.sun.syndication.io.SyndFeedInput
import java.io.InputStreamReader
import com.sun.syndication.feed.synd.SyndEntry
import scala.collection.JavaConversions._
import org.joda.time.DateTime

/**
 * @author anton.safonov
 */

object RssFeedLoader {
  def load(): List[RssFeed] = {
    val feedUrl = new URL("http://news.ycombinator.com/rss")
    val input = new SyndFeedInput()
    val syndFeed = input.build(new InputStreamReader(feedUrl.openStream()))
    val entries: List[SyndEntry] = syndFeed.getEntries.toList.asInstanceOf[List[SyndEntry]]
    entries.map(entry => {
      val description = if(entry.getDescription == null) "" else entry.getDescription.getValue
      RssFeed(1, entry.getLink, entry.getTitle, description, new DateTime(entry.getUpdatedDate))
    })
  }
}