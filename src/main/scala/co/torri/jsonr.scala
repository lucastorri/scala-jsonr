package co.torri

package object jsonr {
    
    private def jsonizeString(a: Any) = a match {
        case s: String => format(""" "%s" """, s).trim //trim: just because it breaks the TextMate color theme
        case a => a.toString
    }

    class JSONBlock(inner: List[(String, Any)]) {
        override def toString = {
            "{" + inner.map { t =>
                val value = jsonizeString(t._2)
                format(""""%s": %s""", t._1, value)
            }.mkString(", ") + "}"
        }
    }
    class JSONArray(el: List[Any]) {
        override def toString = {
            "[" + el.map(jsonizeString).mkString(", ") + "]"
        }
    }

    def $(all: (String, Any)*) = new JSONBlock(List(all: _*))
    def %(all: Any*) = new JSONArray(List(all: _*))
}