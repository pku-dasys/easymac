package io

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

import scala.io.Source
import scala.util.control._

object ReadWT {
  def readFromWTTxt(filePath: String) = {
    val source = Source.fromFile(filePath, "UTF-8")
    val lines = source.getLines().toArray
    source.close
    lines
  }

  // get bits of an multiplier
  def getBits(array: Array[String]): List[Int] = {
    val inputbits = array(0).trim.split(" ")
    val res = inputbits(0).toInt :: inputbits(1).toInt :: Nil
    res
  }

  // get the number of compressors
  def getNumCells(array: Array[String]): List[Int] = {
    val numcells = array(1).trim.split(" ")
    val res = numcells(0).toInt :: Nil
    res
  }

  // get arch description sequence
  def getArch(array: Array[String]): List[Int] = {
    val arraywithoutbits = array.takeRight(array.length - 2)
    val res = arraywithoutbits.map(s => s.trim.split(" ").map(i => i.toInt).toList).reduce(_ ::: _)
    res
  }

  // get depth of a compressor network
  def getDepth(myarch: List[Int]): Int = {
    val len = myarch.length
    var dep = 1
    var ind = 500
    var i = 0
    while (i < len) {
      if (myarch(i) > ind) {
        dep += 1
      }
      ind = myarch(i)
      i += 2
    }
    dep
  }


  def getIn(m: Int, n: Int, myarch: List[Int]): Map[List[Int], List[Int]] = {
    val len = myarch.length
    var edgesin = Map[List[Int], List[Int]]()

    var pos = Map[Int, Int]()

    var cnt = new Array[Int](256)

    for (i <- 0 until (n + m)) {
      pos += i -> 0
      cnt(i) = 0
    }

    var depth = 0
    var ind = 500
    var i = 0
    while (i < len) {
      if (myarch(i) > ind) {
        depth += 1
        for (j <- 0 until (n + m)) {
          cnt(j) = 0
        }
      }
      ind = myarch(i)
      cnt(myarch(i)) += 1
      var tmp = pos(myarch(i))
      edgesin += List(myarch(i), depth, cnt(myarch(i))) -> List(tmp, myarch(i))
      if (myarch(i + 1) == 0) {
        pos += myarch(i) -> (tmp + 2)
      }
      else if (myarch(i + 1) == 1) {
        pos += myarch(i) -> (tmp + 3)
      } else {
        println("Wrong compressor types!")
      }
      i += 2
    }
    edgesin
  }

  def getIn1(m: Int, n: Int, myarch: List[Int]): Map[List[Int], List[Int]] = {
    val len = myarch.length
    var edgesin = Map[List[Int], List[Int]]()

    var pos = Map[Int, Int]()

    var cnt = new Array[Int](256)

    for (i <- 0 until (n + m)) {
      pos += i -> 0
      cnt(i) = 0
    }

    var depth = 0
    var ind = 500
    var i = 0
    while (i < len) {
      if (myarch(i) > ind) {
        depth += 1
        for (j <- 0 until (n + m)) {
          cnt(j) = 0
        }
      }
      ind = myarch(i)
      cnt(myarch(i)) += 1
      var tmp = pos(myarch(i))
      edgesin += List(myarch(i), depth, cnt(myarch(i))) -> List(tmp, myarch(i))
      if (myarch(i + 1) == 0) {
        pos += myarch(i) -> (tmp + 2)
      }
      else if (myarch(i + 1) == 1) {
        pos += myarch(i) -> (tmp + 3)
      } else {
        println("Wrong compressor types!")
      }
      i += 2
    }
    edgesin
  }


  def getOut1(m: Int, n: Int, myarch: List[Int]): Map[List[Int], List[Int]] = {
    val len = myarch.length
    var edgesout = Map[List[Int], List[Int]]()

    var pos = Map[Int, Int]()

    var cnt = new Array[Int](256)

    var abs = 0
    var min = 0
    if (m > n) {
      abs = m - n
      min = n
    } else {
      abs = n - m
      min = m
    }

    for (i <- 0 until (n + m - 1)) {
      if (i < min) {
        var ttmp = i + 1 + 1
        pos += i -> ttmp
      }
      else if (i >= min && i < (min + abs)) {
        var ttmp = (1 + min) + 1
        pos += i -> ttmp
      } else {
        var ttmp = (n + m - i - 1) + 1
        pos += i -> ttmp
      }
    }
    pos += (n + m - 1) -> 0

    var depth = 0
    var ind = 500
    var i = 0
    while (i < len) {
      if (myarch(i) > ind) {
        depth += 1
        for (j <- 0 until (n + m)) {
          cnt(j) = 0
        }
      }
      ind = myarch(i)
      cnt(myarch(i)) += 1
      var tmp = pos(myarch(i))
      var tmp1 = pos(myarch(i) + 1)
      edgesout += List(myarch(i), depth, cnt(myarch(i))) -> List(tmp, myarch(i), tmp1, myarch(i) + 1)
      pos += myarch(i) -> (tmp + 1)
      pos += (myarch(i) + 1) -> (tmp1 + 1)
      i += 2
    }
    edgesout
  }

  def getOut(m: Int, n: Int, myarch: List[Int]): Map[List[Int], List[Int]] = {
    val len = myarch.length
    var edgesout = Map[List[Int], List[Int]]()

    var pos = Map[Int, Int]()

    var cnt = new Array[Int](256)

    var abs = 0
    var min = 0
    if (m > n) {
      abs = m - n
      min = n
    } else {
      abs = n - m
      min = m
    }

    for (i <- 0 until (n + m)) {
      if (i < min) {
        pos += i -> (i + 1)
      }
      else if (i >= min && i < (min + abs)) {
        pos += i -> min
      } else {
        pos += i -> (m + n - i - 1)
      }
    }

    var depth = 0
    var ind = 500
    var i = 0
    while (i < len) {
      if (myarch(i) > ind) {
        depth += 1
        for (j <- 0 until (n + m)) {
          cnt(j) = 0
        }
      }
      ind = myarch(i)
      cnt(myarch(i)) += 1
      var tmp = pos(myarch(i))
      var tmp1 = pos(myarch(i) + 1)
      edgesout += List(myarch(i), depth, cnt(myarch(i))) -> List(tmp, myarch(i), tmp1, myarch(i) + 1)
      pos += myarch(i) -> (tmp + 1)
      pos += (myarch(i) + 1) -> (tmp1 + 1)
      i += 2
    }
    edgesout
  }


  /**
   * A procedure to help generate a compressor network for a multiplication unit.
   *
   * An `m`-bit multiplicand multiplying an `n`-bit multiplier generates `m*n`-bit partial products.
   * The bits in the partial products are represented by dots, intially in a trapezoidal shape.
   * In the code, we simulate the dot compression process using the compressors in `myarch`.
   * During compression, any new bit is stacked in a higher row number in the corresponding column.
   * The row numbers of remaining dots in each column are returned to the generator.
   *
   * @param m      The bit width of the multiplicand.
   * @param n      The bit width of the multiplier.
   * @param myarch The (reversed) sequence representation of the compressor network.
   * @return The count and row number of remaining dots in each column.
   */
  def getRes(m: Int, n: Int, myarch: List[Int]): Map[Int, List[Int]] = {
    var res = Map[Int, List[Int]]() // column number -> a list of row numbers with -1 padding

    val len = myarch.length / 2 // the number of compressors

    var compressorCount = Map[List[Int], Int]() // compressor code -> the number of compressors
    var rowNumber = Map[Int, Int]() // column number -> current row number

    var initDots = new Array[Int](m + n) // the initial dots in a trapezoidal shape
    var compDots = new Array[Int](m + n) // the remaining dots after compression

    val diff = Math.abs(m - n)
    val min = Math.min(m, n)

    // generate the dots in the initial partial products
    for (col <- 0 until (n + m - 1)) {
      if (col < min) {
        initDots(col) = col + 1
        rowNumber += col -> initDots(col)
      }
      else if (col >= min && col < (min + diff)) {
        initDots(col) = min
        rowNumber += col -> initDots(col)
      } else {
        initDots(col) = n + m - 1 - col
        rowNumber += col -> initDots(col)
      }
      compressorCount += List(col, 0) -> 0
      compressorCount += List(col, 1) -> 0
    }

    // count the compressors in `myarch`
    for (i <- 0 until len) {
      val idx = 2 * i
      val code = List(myarch(idx), myarch(idx + 1))
      val count = compressorCount(code)
      compressorCount += code -> (count + 1)
    }

    // update the row numbers in the columns by scanning `myarch`
    for (i <- 0 until len) {
      val idx = 2 * i
      val currColNum = myarch(idx)
      val nextColNum = currColNum + 1
      val currRowNum = rowNumber(currColNum)
      val nextRowNum = rowNumber(nextColNum)
      rowNumber += currColNum -> (currRowNum + 1)
      rowNumber += nextColNum -> (nextRowNum + 1)
    }

    // check for over-compression and lack-of-compression
    compDots(0) = 1
    for (col <- 1 until (n + m - 1)) {
      var currC22Count = compressorCount(List(col, 0))
      var currC32Count = compressorCount(List(col, 1))
      var prevC22Count = compressorCount(List(col - 1, 0))
      var prevC32Count = compressorCount(List(col - 1, 1))
      compDots(col) = initDots(col) + prevC22Count + prevC32Count + (1 - 2) * currC22Count + (1 - 3) * currC32Count
      if (compDots(col) < 0 || compDots(col) > 2) {
        println("Wrong Compressors Structure! At column = " + col + " and res=" + compDots(col))
      }
    }

    // collect the row numbers of the one or two remaining dots in each column
    for (col <- 0 until (n + m - 1)) {
      if (compDots(col) == 1) {
        res += col -> List(rowNumber(col) - 1, -1) // padding with -1 for a single remaining dot
      }
      else if (compDots(col) == 2) {
        res += col -> List(rowNumber(col) - 2, rowNumber(col) - 1) // two remaining dots
      } else {
        println("Wrong Compressors Results! At column = " + col)
      }
    }
    res
  }

}
