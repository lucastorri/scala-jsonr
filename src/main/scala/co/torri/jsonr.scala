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
            case null           => nullElement
            case None           => emptyElement
            case n: Unit        => emptyElement
            case Some(s)        => JSONElement(s)
            case n: Number      => PrimitiveElement(n)
            case b: Boolean     => PrimitiveElement(b)
            case c: Char        => PrimitiveElement(c)
            case s: String      => StringElement(s)
            case m: IMap[_, _]  => MapElement(m)
            case i: Iterable[_] => ArrayElement(i.toList)
            case Tuple1(e)      => JSONElement(e)
            case t: Tuple2[_,_] => PairElement(t)
            case TupleType(t)   => ArrayElement(t)
            case e: JSONElement => e
            case o              => ClassElement(o)
        }
    }
    
    object nullElement extends JSONElement {
        protected def construct(b: StringBuilder) = b.append("null")
    }
    
    object emptyElement extends JSONElement {
        protected def construct(b: StringBuilder) = b.append("\"\"")
    }

    case class StringElement(s: String) extends JSONElement {
        private def escape(s: String) = s
        protected def construct(b: StringBuilder) = b.append("\"").append(escape(s)).append("\"")
    }

    case class PrimitiveElement[ElementType](e: ElementType) extends JSONElement {
        protected def construct(b: StringBuilder) = b.append(e.toString)
    }
    
    case class PairElement[Key,Value](p: (Key, Value)) extends JSONElement {
        protected def construct(b: StringBuilder) = b.append(JSONElement(p._1)).append(": ").append(JSONElement(p._2))
    }
    
    abstract class SequenceElement(begin: String, end: String) extends JSONElement {
        def elements: Iterable[JSONElement]
        protected def construct(b: StringBuilder) = {
            var allE = elements
            b.append(begin)
            allE.headOption.map { h =>
                allE.tail.foldLeft(b.append(h)) { case (b, e) =>
                    b.append(", ").append(e)
                }
            }
            b.append(end)
        }
    }
    
    case class ArrayElement[IterableType](i: Iterable[IterableType]) extends SequenceElement("[", "]") {
        def elements = i.map(JSONElement.apply)
    }

    case class MapElement(m: IMap[_,_]) extends SequenceElement("{", "}") {
        def elements = m.map(JSONElement.apply).toList
    }
    
    case class ClassElement[ClassType](o: ClassType) extends SequenceElement("{", "}") {
        def elements = {
            val c = o.getClass
            c.getDeclaredFields.view.filterNot(_.getName.contains("$")).filter { f =>
                try Option(c.getDeclaredMethod(f.getName)).map(_ => true).getOrElse(false)
                catch { case _ => false }
            }.map { f =>
                JSONElement(f.getName, c.getDeclaredMethod(f.getName).invoke(o))
            }
        }
    }
    
    
    implicit def any2json(a: Any) = JSONElement(a.asInstanceOf[AnyRef])
	

    def $(all: (String, Any)*) = MapElement(all.toMap)
    def %(all: Any*) = new ArrayElement(all)
	
}