package com.github.tonek.anyread

import util.matching.Regex

trait Candidates {
  def isCandidate(node: NodeWrapper): Boolean
}

trait RegexCandidates extends Candidates {
  def regexStr: String
  lazy val regex = new Regex(regexStr)

  def isCandidate(node: NodeWrapper) = {
    val matchStr = node.id.getOrElse("") + node.classes.getOrElse("")

    regex.findFirstIn(matchStr).isDefined
  }

  def getStringToMatch(node: NodeWrapper): String
}

class IdClassRegexCandidates(val regexStr: String) extends RegexCandidates {
  def getStringToMatch(node: NodeWrapper) = node.id.getOrElse("") + node.classes.getOrElse("")
}

class IdRegexCandidates(val regexStr: String) extends RegexCandidates {
  def getStringToMatch(node: NodeWrapper) = node.id.getOrElse("")
}

class ClassRegexCandidates(val regexStr: String) extends RegexCandidates {
  def getStringToMatch(node: NodeWrapper) = node.classes.getOrElse("")
}

class InnerHtmlRegexCandidates(val regexStr: String) extends RegexCandidates {
  def getStringToMatch(node: NodeWrapper) = node.innerHtml()
}

class TagCandidates(tags: Set[String]) extends Candidates {

  def this(tag: String) = this(Set(tag))
  def this(tagsArray: String*) = this(Set(tagsArray:_*))

  def isCandidate(node: NodeWrapper) = tags.contains(node.tagName.toLowerCase)
}

object UnlikelyCandidates extends IdClassRegexCandidates (
  "combx|comment|community|disqus|extra|foot|header|menu|remark|rss|shoutbox|sidebar|sponsor|ad-break|agegate|" +
    "pagination|pager|popup|tweet|twitter"
)

object PossibleCandidates extends IdClassRegexCandidates("and|article|body|column|main|shadow")

object PositiveClassCandidates extends ClassRegexCandidates(
  "article|body|content|entry|hentry|main|page|pagination|post|text|blog|story"
)
object PositiveIdCandidates extends IdRegexCandidates(
  "article|body|content|entry|hentry|main|page|pagination|post|text|blog|story"
)

object NegativeClassCandidates extends ClassRegexCandidates(
  "combx|comment|com-|contact|foot|footer|footnote|masthead|media|meta|outbrain|promo|related|scroll|shoutbox|" +
    "sidebar|sponsor|shopping|tags|tool|widget"
)
object NegativeIdCandidates extends IdRegexCandidates(
  "combx|comment|com-|contact|foot|footer|footnote|masthead|media|meta|outbrain|promo|related|scroll|shoutbox|" +
    "sidebar|sponsor|shopping|tags|tool|widget"
)

object Body extends TagCandidates("body")

object ToScoreTagCandidates extends TagCandidates("p", "td", "pre")

object ParagraphReplaceableCandidates extends Candidates {
  val regex = new InnerHtmlRegexCandidates("<(a|blockquote|dl|div|img|ol|p|pre|table|ul)")
  val tagChecker = new TagCandidates("div")

  def isCandidate(node: NodeWrapper) = tagChecker.isCandidate(node) && !regex.isCandidate(node)
}
