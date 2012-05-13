package com.github.tonek.anyread

import org.scalatest.FunSuite
import org.jsoup.Jsoup

/**
 * @author anton.safonov
 */

class PrettifySpec extends FunSuite with Logging with NodesUtil{

  val unlikelyCandidate = nodeWrapper("qwe", "combx")

  val likelyCandidate = nodeWrapper("qwe", "article")

  test("unlikely candidates are filtered out") {
    assert(UnlikelyCandidates.isCandidate(unlikelyCandidate))
    assert(!UnlikelyCandidates.isCandidate(likelyCandidate))
  }

  test("possible candidates should stay") {
    assert(!PossibleCandidates.isCandidate(unlikelyCandidate))
    assert(PossibleCandidates.isCandidate(likelyCandidate))

    val p = new Prettify(JsoupNodes)
    p.getContent(unlikelyCandidate)
  }
}
