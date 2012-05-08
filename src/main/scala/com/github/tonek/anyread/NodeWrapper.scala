package com.github.tonek.anyread

import scala.collection.JavaConversions._
import scala.Predef._
import org.jsoup.parser.Tag
import org.jsoup.nodes.{TextNode, Element}


trait NodeWrapper {
  def id: Option[String]

  def classes: Option[String]
  def classes(classes: String)

  def tagName: String

  def tagName(tag: String)

  def removeFromParent()

  def allChildren(): List[NodeWrapper]

  def allChildrenWithTag(tag: String): List[NodeWrapper]

  def children(): List[NodeWrapper]

  def appendChild(node: NodeWrapper)

  def innerHtml(): String
  def innerHtml(html: String)

  def text(): String

  def textChildren(): List[TextNodeWrapper]

  def parent: Option[NodeWrapper]

  def wrapWithTag(tag: String): NodeWrapper

  def toPrintableString(): String

  override def toString = "<%s id='%s' class='%s'/>".format(tagName, id.getOrElse(""), classes.getOrElse(""))
}

trait TextNodeWrapper {
  def wrapWithTag(tag: String): NodeWrapper
}

class JsoupElementWrapper(val element: Element) extends NodeWrapper {
  val id = Some(element.id())
  def classes = {
    val cls = element.className()
    if (cls == null || cls.trim.isEmpty) None else Some(cls)
  }


  def classes(classes: String) = element.classNames(Set(classes))

  def tagName = element.tag().getName
  lazy val parent = if (element.parent() == null) None else Some(new JsoupElementWrapper(element.parent()))

  def removeFromParent() {
    if (element.parent() != null) {
      element.remove()
    }
  }

  def innerHtml() = element.html()
  def innerHtml(html: String) = element.html(html)

  def text() = element.text()

  def tagName(tag: String) {
    element.tagName(tag)
  }

  def allChildren() = element.getAllElements.map(new JsoupElementWrapper(_)).toList

  def allChildrenWithTag(tag: String) = element.getElementsByTag(tag).map(new JsoupElementWrapper(_)).toList

  def children() = element.children().toList.map(new JsoupElementWrapper(_))


  def appendChild(node: NodeWrapper) = element.appendChild(node.asInstanceOf[JsoupElementWrapper].element)

  def textChildren() = {
    element.childNodes()
      .filter(_.isInstanceOf[TextNodeWrapper])
      .map(node => new JsoupTextNodeWrapper(node.asInstanceOf[TextNode]).asInstanceOf[TextNodeWrapper]).toList
  }

  def wrapWithTag(tag: String) = {
    val newElement = new Element(Tag.valueOf(tag), element.baseUri())
    newElement.append(element.html())
    new JsoupElementWrapper(newElement)
  }


  def toPrintableString() = element.toString

  override def equals(obj: Any) = {
    obj.isInstanceOf[JsoupElementWrapper] &&
      obj.asInstanceOf[JsoupElementWrapper].element == element
  }

  override def hashCode() = element.hashCode()
}

class JsoupTextNodeWrapper(node: TextNode) extends TextNodeWrapper{
  def wrapWithTag(tag: String): NodeWrapper = {
    val element = new Element(Tag.valueOf(tag), node.baseUri())
    element.attr("style", "display:inline;").classNames(Set("any-read-text"))
    element.text(node.text())
    node.replaceWith(element)
    new JsoupElementWrapper(element)
  }
}

trait Nodes {
  def create(tag: String): NodeWrapper
}

object JsoupNodes extends Nodes {
  def create(tag: String) = {
    val element = new Element(Tag.valueOf(tag), "")
    new JsoupElementWrapper(element)
  }
}


