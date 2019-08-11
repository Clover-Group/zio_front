import java.io.{ ByteArrayInputStream }
import zio.{ Chunk }

import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.ipc.{ ArrowStreamReader }

package object ArrowPkg {

  def deserialize(din: Chunk[Array[Byte]]): Chunk[ArrowStreamReader] =
    for {
      arr    <- din
      alloc  = new RootAllocator(Integer.MAX_VALUE)
      stream = new ByteArrayInputStream(arr)
      reader = new ArrowStreamReader(stream, alloc)

    } yield reader

}
