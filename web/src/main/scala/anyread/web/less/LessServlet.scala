package anyread.web.less

/**
 * @author anton.safonov
 */

class LessServlet extends com.asual.lesscss.LessServlet
{
  override def init()
  {
    super.init()
    cache = false
    mimeTypes.put("less", "text/css")
  }
}
