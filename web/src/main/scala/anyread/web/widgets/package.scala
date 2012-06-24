package anyread.web

import net.liftweb.util._
import org.joda.time.format.DateTimeFormatterBuilder
import xml.NodeSeq
import java.util.{Calendar, Date}
import net.liftweb.http.Templates
import org.joda.time.DateTime

package object widgets extends WidgetsTrait

trait WidgetsTrait extends TimeHelpers with StringHelpers with ListHelpers
with SecurityHelpers with BindHelpers with HttpHelpers
with IoHelpers with BasicTypesHelpers
with ClassHelpers with ControlHelpers {
  def dateTimeFormatter = new DateTimeFormatterBuilder()
    .appendDayOfMonth(1).appendLiteral(' ').appendMonthOfYearShortText().appendLiteral(" в ")
    .appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).toFormatter

  def hiddenT(path: List[String]): NodeSeq = Templates("templates-hidden" :: path).get
  def hiddenT(path: String*): NodeSeq = hiddenT(path.toList)

  def format(date: Date): String = {
    dateTimeFormatter.print(date.getTime)
  }

  def format(calendar: Calendar): String = format(calendar.getTime)

  def format(dateTime: DateTime): String = format(dateTime.toDate)
}
