package co.torri

package object jsonr {
    
    import scala.collection.{ Map => IMap }
    
    object TupleType {
        type Tuples = { def productIterator: Iterator[_] }
        def unapply(a: Any) : Option[Iterable[Any]] = a match {
            case t: Tuples if t.getClass.getName.matches(".*scala.Tuple\\d+") => Some(t.productIterator.toIterable)
            case _ => None
        }
    }

    trait JSONElement {
        protected def construct(b: StringBuilder) : StringBuilder
        override def toString = construct(new StringBuilder).toString
        def toJson = this
    }
    
    object JSONElement {
        def apply(a: Any) : JSONElement = a match {
            case e: JSONElement => e
            case null           => nullElement
            case None           => emptyElement
            case n: Unit        => emptyElement
            case n: Number      => PrimitiveElement(n)
            case b: Boolean     => PrimitiveElement(b)
            case c: Char        => PrimitiveElement(c)
            case s: String      => StringElement(s)
            case Some(s)        => JSONElement(s)
            case Tuple1(e)      => JSONElement(e)
            case t: Tuple2[_,_] => PairElement(t)
            case m: IMap[_, _]  => MapElement(m)
            case i: Iterable[_] => ArrayElement(i)
            case TupleType(t)   => ArrayElement(t)
            case o              => ObjectElement(o)
        }
    }
    
    object nullElement extends JSONElement {
        protected def construct(b: StringBuilder) = b.append("null")
    }
    
    object emptyElement extends JSONElement {
        protected def construct(b: StringBuilder) = b.append("\"\"")
    }

    case class StringElement(s: String) extends JSONElement {
        private def escaped = s
        protected def construct(b: StringBuilder) = b.append("\"").append(escaped).append("\"")
    }

    case class PrimitiveElement[ElementType](e: ElementType) extends JSONElement {
        protected def construct(b: StringBuilder) = b.append(e.toString)
    }
    
    case class PairElement[Key,Value](p: (Key, Value)) extends JSONElement {
        protected def construct(b: StringBuilder) = b.append(JSONElement(p._1)).append(": ").append(JSONElement(p._2))
    }
    
    abstract class SequenceElement(begin: String, end: String) extends JSONElement {
        def elements: Iterable[JSONElement]
        protected def construct(b: StringBuilder) =
            Some(b.append(begin)).flatMap(_ => elements.headOption).map { h =>
                elements.tail.foldLeft(b.append(h)) { case (b, e) =>
                    b.append(", ").append(e)
                }
            }.getOrElse(b).append(end)
    }
    
    case class ArrayElement[IterableType](i: Iterable[IterableType]) extends SequenceElement("[", "]") {
        lazy val elements = i.map(JSONElement.apply)
    }

    case class MapElement(m: IMap[_,_]) extends SequenceElement("{", "}") {
        lazy val elements = m.map(JSONElement.apply).toList
    }
    
    case class ObjectElement[ClassType](o: ClassType) extends SequenceElement("{", "}") {
        lazy val elements =
            o.getClass.getDeclaredFields.filterNot(_.getName.contains("$")).flatMap { f =>
                try Option(o.getClass.getDeclaredMethod(f.getName)).map(m => JSONElement(f.getName, m.invoke(o)))
                catch { case _ => None }
            }.toList
    }
    
    
    implicit def any2json(a: Any) = JSONElement(a)

    def $(all: (String, Any)*) = MapElement(all.toMap)
    def %(all: Any*) = new ArrayElement(all)

}
