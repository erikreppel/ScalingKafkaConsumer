package utilities

import scala.collection.immutable.Map
import javax.xml.bind.DatatypeConverter
import kafka.utils.Logging


object DvstHelpers extends Logging {

  def bytesToHex(bytes : Array[Byte]) =
    bytes.map{ b => String.format("%02X", java.lang.Byte.valueOf(b)) }.mkString("")

  def hex2int (hex: String): Int = Integer.parseInt(hex, 16)

  def hex2bytes(hex: String): Array[Byte] = {
    hex.replaceAll("[^0-9A-Fa-f]", "")
       .sliding(2, 2)
       .toArray
       .map(Integer.parseInt(_, 16)
                   .toByte)
  }

  def getPrefix(pbMessage: Array[Byte], prefixLength: Int = 20): Map[String, Array[Byte]] = {
    val prefix = pbMessage.slice(0, prefixLength)

    val prefixMap = Map[String, Array[Byte]](
      "pb_prefix_ver"    -> prefix.slice(0,4),
      "pb_msg_type"      -> prefix.slice(4, 8),
      "pb_msg_len_bytes" -> prefix.slice(8, 12),
      "dvst_msg_seq_num" -> prefix.slice(12, 16),
      "rfu0"             -> prefix.slice(16, 20)
    )
    return prefixMap
  }

  def getProtobufArray(pbMessage: Array[Byte], prefixLength: Int = 20): Array[Byte] = {
    val pbArray = pbMessage.slice(prefixLength, pbMessage.length)
    return pbArray
  }

  def toAscii(hex: String) = {
    new String(DatatypeConverter.parseHexBinary(hex))
}

}
