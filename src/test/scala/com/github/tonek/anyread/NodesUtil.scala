package com.github.tonek.anyread

import org.jsoup.parser.Tag
import org.jsoup.nodes.{Attributes, Element}
import java.util.Collections

/**
 * @author anton.safonov
 */

trait NodesUtil {
  def nodeWrapper(id: String, classes: String, tag: String = "span"): NodeWrapper = {
    val attrs = new Attributes()
    attrs.put("id", id)

    val element = new Element(Tag.valueOf(tag), "", attrs)
    element.classNames(Collections.singleton(classes))

    new JsoupElementWrapper(element)
  }
}
