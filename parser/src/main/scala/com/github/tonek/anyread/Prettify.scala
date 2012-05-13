package com.github.tonek.anyread

import scala.math._
import scala.None
import util.matching.Regex


/**
 * @author anton.safonov
 */

class Prettify(nodes: Nodes) extends Logging {

  private val scores = scala.collection.mutable.Map[NodeWrapper, Float]()

  def getContent(document: NodeWrapper): NodeWrapper = {
    debugPretty(2, "initial processing ...")
    val paragraphs: List[NodeWrapper] = document.allChildren().filter(processParagraphs _)

    debugPretty(2, "selecting top candidates ...")

    val topCandidates = selectTopCandidates(paragraphs)

    val topCandidate = selectTopCandidate(topCandidates) match {
      case Some(candidate) if (candidate.tagName.toLowerCase != "body") => candidate
      case _ => {
        val candidate = document.wrapWithTag("div")
        document.innerHtml("")
        candidate
      }
    }

    val article = nodes.create("div")

    processSiblings(topCandidate, article)

    article
  }


  def processSiblings(topCandidate: NodeWrapper, article: NodeWrapper) {
    val siblingScoreThreshold = max(10, score(topCandidate) * 0.2)
    val siblings = topCandidate.parent.toList.flatMap(_.children())

    for {
      sibling <- siblings
    } {
      var append = topCandidate == sibling
      var contentBonus: Float = 0

      /* Give a bonus if sibling nodes and top candidates have the example same classname */
      if (topCandidate.classes.isDefined && sibling.classes == topCandidate.classes) {
        contentBonus = contentBonus + (score(topCandidate) * 0.2).toFloat;
      }

      if (!append && score(sibling) + contentBonus >= siblingScoreThreshold) {
        append = true;
      }

      if (sibling.tagName.equalsIgnoreCase("p")) {
        val linkDensity = getLinksDensity(sibling);
        val nodeContent = getInnerText(sibling);
        val nodeLength = nodeContent.length;

        if (nodeLength > 80 && linkDensity < 0.25) {
          append = true;
        } else if (nodeLength < 80 && linkDensity == 0 && new Regex("""\.( |$)""").findFirstIn(nodeContent).isDefined) {
          append = true;
        }
      }

      if (append) {
        debugPretty(4, "Appending node: " + sibling);

        if (!Set("div", "p").contains(sibling.tagName.toLowerCase)) {
          /* We have a node that isn't a common block level element, like a form or td tag. Turn it into a div so it doesn't get filtered out later by accident. */

          debugPretty(4, "Altering siblingNode of " + sibling.tagName + " to div.")
          sibling.tagName("div")
        }

        /* To ensure a node does not interfere with readability styles, remove its classnames */
        sibling.classes("")

        /* Append sibling and subtract from our list because it removes the node when you append to another node */
        article.appendChild(sibling);
      }
    }
  }

  def selectTopCandidate(topCandidates: scala.List[NodeWrapper]): Option[NodeWrapper] = {
    topCandidates.foldLeft(Option[NodeWrapper](null))(
      (prev, current) => {
        getLinksDensity(current) match {
          case density if (density >= 1) => setScore(current, 0)
          case density => setScore(current,  score(current) * (1 - density))
        }

        debugPretty(4, "candidate with score %.2f : %s".format(score(current), current))

        prev match {
          case Some(p) if (score(current) > score(p)) => Some(current)
          case None => Some(current)
          case _ => prev
        }
      }
    )
  }

  def selectTopCandidates(paragraphs: List[NodeWrapper]): List[NodeWrapper] = {
    val rawList: List[List[NodeWrapper]] = for {
      paragraph <- paragraphs
      parent <- paragraph.parent
      grandParent = parent.parent
      innerText = getInnerText(paragraph)
      if (innerText.length > 25)
    } yield {
      initScore(parent)
      grandParent.foreach(initScore _)

      /* Add a point for the paragraph itself as a base. */
      var contentScore = 1

      /* Add points for any commas within this paragraph */
      contentScore = contentScore + innerText.split(',').length

      /* For every 100 characters in this paragraph, add another point. Up to 3 points. */
      contentScore = contentScore + min(innerText.length / 100, 3)

      /* Add the score to the parent. The grandparent gets half. */
      addScore(parent, contentScore)
      grandParent.foreach(addScore(_, contentScore / 2))
      List(parent) ::: grandParent.toList
    }

    rawList.flatten
  }

  def isUnlikelyCandidate(child: NodeWrapper): Boolean = {
    UnlikelyCandidates.isCandidate(child) && !PossibleCandidates.isCandidate(child) && !Body.isCandidate(child)
  }

  def processParagraphs(node: NodeWrapper): Boolean = {
    if (isUnlikelyCandidate(node)) {
      node.removeFromParent()
      debugPretty(4, "removing unlikely candidate: " + node)
      false

    } else if (ToScoreTagCandidates.isCandidate(node)) {
      true;

    } else if (ParagraphReplaceableCandidates.isCandidate(node)) {
      node.tagName("p")
      true

    } else if (ParagraphReplaceableCandidates.tagChecker.isCandidate(node)) {
      wrapTextChildren(node)
      false

    } else {
      false
    }
  }

  private val tagScores: Map[String, Int] = Map(
    "div" -> 5,
    "pre" -> 3, "td" -> 3, "blockquote" -> 3,
    "address" -> -3, "ol" -> -3, "ul" -> -3, "dl" -> -3, "dd" -> -3, "dt" -> -3, "li" -> -3, "form" -> -3,
    "h1" -> -5, "h2" -> -5, "h3" -> -5, "h4" -> -5, "h5" -> -5, "h6" -> -5, "th" -> -5
  )

  def initScore(node: NodeWrapper) {
    if (isScoreInit(node)) {
      return
    }

    val tag = node.tagName.toLowerCase

    if (tagScores.contains(tag)) {
      addScore(node, tagScores(tag))
    }

    addScore(node, getClassScore(node))
  }

  private val classesScores: Map[Candidates, Int] = Map(
    PositiveClassCandidates -> 25, PositiveIdCandidates -> 25,
    NegativeClassCandidates -> -25, NegativeIdCandidates -> -25
  )

  def getClassScore(node: NodeWrapper): Int = {
    var totalScore = 0;
    for {
      entry <- classesScores
      if (entry._1.isCandidate(node))
    } {
      totalScore = totalScore + entry._2
    }
    totalScore
  }

  def getLinksDensity(node: NodeWrapper): Float = {
    getInnerText(node).length match {
      case 0 => 0
      case textLen => {
        val links = node.allChildrenWithTag("a")
        val linksLen = links.foldLeft(0)(_ + getInnerText(_).length)
        linksLen / textLen
      }
    }
  }

  def getInnerText(paragraph: NodeWrapper): String = {
    paragraph.text() match {
      case text if (text == null) => ""
      case text => text.trim
    }
  }

  def addScore(node: NodeWrapper, score: Float) {
    scores.get(node) match {
      case Some(curScore) => scores.update(node, curScore + score)
      case _ => scores += node -> score
    }
  }

  def setScore(node: NodeWrapper, score: Float) {
    scores += node -> score
  }

  def isScoreInit(node: NodeWrapper): Boolean = scores.contains(node)

  def score(node: NodeWrapper): Float = scores.get(node).getOrElse(0)

  def wrapTextChildren(node: NodeWrapper) = {
    for (textNode <- node.textChildren()) {
      textNode.wrapWithTag("p")
    }
  }
}


