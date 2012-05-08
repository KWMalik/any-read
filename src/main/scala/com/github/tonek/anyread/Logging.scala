package com.github.tonek.anyread

import org.slf4j.LoggerFactory
import scala._

/**
 * @author anton.safonov
 */

trait Logging {
  val statsWidth: Int = 120

  private[this] val logger = LoggerFactory.getLogger(getClass)

  def trace(message: => String) = if (logger.isTraceEnabled) logger.trace(message)

  def trace(message: => String, ex: Throwable) = if (logger.isTraceEnabled) logger.trace(message, ex)

  def debug(message: => String) = if (logger.isDebugEnabled) logger.debug(message)

  def debug(message: => String, ex: Throwable) = if (logger.isDebugEnabled) logger.debug(message, ex)

  def info(message: => String) = if (logger.isInfoEnabled) logger.info(message)

  def info(message: => String, ex: Throwable) = if (logger.isInfoEnabled) logger.info(message, ex)

  def warn(message: => String) = if (logger.isWarnEnabled) logger.warn(message)

  def warn(message: => String, ex: Throwable) = if (logger.isWarnEnabled) logger.warn(message, ex)

  def error(ex: Throwable) = if (logger.isErrorEnabled) logger.error(ex.toString, ex)

  def error(message: => String) = if (logger.isErrorEnabled) logger.error(message)

  def error(message: => String, ex: Throwable) = if (logger.isErrorEnabled) logger.error(message, ex)

  def debugPretty(indent: Int, message: => String) = if (logger.isDebugEnabled) {
    val msg = new StringBuilder()
    for (i <- 0 until indent) {
      msg.append("=")
    }
    if (indent > 0) {
      msg.append(" ")
    }
    logger.debug(msg.append(message).toString())
  }
}
