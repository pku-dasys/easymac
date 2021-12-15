package io

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

import scala.io.Source
import scala.util.control._

object ReadWT {
  def readFromWTTxt(filePath:String) = {
    val source = Source.fromFile(filePath, "UTF-8")
    val lines = source.getLines().toArray
    source.close
    lines
  }
  
  // get bits of an multiplier
  def getBits(array:Array[String]) : List[Int] = {
    val inputbits = array(0).trim.split(" ")
    val res = inputbits(0).toInt :: inputbits(1).toInt :: Nil
    res
  }

  // get the number of compressors
  def getNumCells(array:Array[String]) : List[Int] = {
    val numcells = array(1).trim.split(" ")
    val res = numcells(0).toInt:: Nil
    res
  }

  // get arch description sequence
  def getArch(array:Array[String]) : List[Int] = {
    val arraywithoutbits = array.takeRight(array.length-2)
    val res = arraywithoutbits.map(s => s.trim.split(" ").map(i => i.toInt).toList).reduce(_ ::: _)
    res
  }

  // get depth of a compressor network
  def getDepth(myarch:List[Int]) : Int = {
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


  def getIn(m:Int, n:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
    val len = myarch.length
    var edgesin = Map[List[Int], List[Int]]()

    var pos = Map[Int, Int]()

    var cnt = new Array[Int](256)

    for (i <- 0 until (n+m)) {
      pos += i -> 0
      cnt(i) = 0
    }

    var depth = 0
    var ind = 500
    var i = 0
    while(i < len) {
      if (myarch(i) > ind) {
        depth += 1
        for (j <- 0 until (n+m)) {
          cnt(j) = 0
        }
      }
      ind = myarch(i)
      cnt(myarch(i)) += 1
      var tmp = pos(myarch(i))
      edgesin += List(myarch(i), depth, cnt(myarch(i))) -> List(tmp, myarch(i))
      if (myarch(i+1) == 0) {      
        pos += myarch(i) -> (tmp + 2)
      }
      else if (myarch(i+1) == 1) {       
        pos += myarch(i) -> (tmp + 3)
      } else {
        println("Wrong compressor types!")
      }
      i += 2
    }
    edgesin
  }

  def getIn1(m:Int, n:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
    val len = myarch.length
    var edgesin = Map[List[Int], List[Int]]()

    var pos = Map[Int, Int]()

    var cnt = new Array[Int](256)

    for (i <- 0 until (n+m)) {
      pos += i -> 0
      cnt(i) = 0
    }

    var depth = 0
    var ind = 500
    var i = 0
    while(i < len) {
      if (myarch(i) > ind) {
        depth += 1
        for (j <- 0 until (n+m)) {
          cnt(j) = 0
        }
      }
      ind = myarch(i)
      cnt(myarch(i)) += 1
      var tmp = pos(myarch(i))
      edgesin += List(myarch(i), depth, cnt(myarch(i))) -> List(tmp, myarch(i))
      if (myarch(i+1) == 0) {      
        pos += myarch(i) -> (tmp + 2)
      }
      else if (myarch(i+1) == 1) {       
        pos += myarch(i) -> (tmp + 3)
      } else {
        println("Wrong compressor types!")
      }
      i += 2
    }
    edgesin
  }


  def getOut1(m:Int, n:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
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

    for (i <- 0 until (n+m-1)) {
      if (i < min) {
        var ttmp = i+1+1
        pos += i -> ttmp
      }
      else if (i >= min && i < (min + abs)) {
        var ttmp = (1 + min)+1
        pos += i -> ttmp
      } else {
        var ttmp = (n+m-i-1)+1 
        pos += i -> ttmp
      }
    }
    pos += (n+m-1) -> 0

    var depth = 0
    var ind = 500
    var i = 0
    while(i < len) {
      if (myarch(i) > ind) {
        depth += 1
        for (j <- 0 until (n+m)) {
          cnt(j) = 0
        }
      }
      ind = myarch(i)
      cnt(myarch(i)) += 1
      var tmp = pos(myarch(i))
      var tmp1 = pos(myarch(i)+1)
      edgesout += List(myarch(i), depth, cnt(myarch(i))) -> List(tmp, myarch(i), tmp1, myarch(i)+1)
      pos += myarch(i) -> (tmp+1)
      pos += (myarch(i)+1) -> (tmp1+1)
      i += 2
    }
    edgesout
  }

  def getOut(m:Int, n:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
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

    for (i <- 0 until (n+m)) {
      if (i < min) {
        pos += i -> (i+1) 
      }
      else if (i >= min && i < (min + abs)) {
        pos += i -> min
      } else {
        pos += i -> (m+n-i-1)
      }
    }

    var depth = 0
    var ind = 500
    var i = 0
    while(i < len) {
      if (myarch(i) > ind) {
        depth += 1
        for (j <- 0 until (n+m)) {
          cnt(j) = 0
        }
      }
      ind = myarch(i)
      cnt(myarch(i)) += 1
      var tmp = pos(myarch(i))
      var tmp1 = pos(myarch(i)+1)
      edgesout += List(myarch(i), depth, cnt(myarch(i))) -> List(tmp, myarch(i), tmp1, myarch(i)+1)
      pos += myarch(i) -> (tmp+1)
      pos += (myarch(i)+1) -> (tmp1+1)
      i += 2
    }
    edgesout
  }

  
  def getRes(m:Int, n:Int, myarch:List[Int]) : Map[Int, List[Int]] = {
    val len = myarch.length
    var res = Map[Int, List[Int]]()
    var compressors = Map[List[Int], Int]()

    var pos = Map[Int, Int]()

    var cnt = new Array[Int](256)

    var resi = new Array[Int](256)

    var abs = 0
    var min = 0
    var max = 0
    if (m > n) {
      abs = m - n
      min = n
    } else {
      abs = n - m
      min = m
    }

    for (i <- 0 until (n+m)) {
      if (i < min) {
        pos += i -> (i+1) 
        cnt(i) = i+1
      }
      else if (i >= min && i < (min + abs)) {
        pos += i -> min
        cnt(i) = min
      } else {
        cnt(i) = m + n -i-1
        pos += i -> (m + n -i-1)
      }
      compressors += List(i, 0) -> 0
      compressors += List(i, 1) -> 0
    }

    var i = 0
    while(i < len) {
      var tmp = compressors(List(myarch(i), myarch(i+1)))
      compressors += List(myarch(i), myarch(i+1)) -> (tmp+1)
      i += 2
    }

    i = 0
    while(i < len) {
      var tmp = pos(myarch(i))
      var tmp1 = pos(myarch(i)+1)
      pos += myarch(i) -> (tmp+1)
      pos += (myarch(i)+1) -> (tmp1+1)
      i += 2
    }

    resi(0) = 1
    for (j <- 1 until (n+m-1)) {
      var num0 = compressors(List(j, 0))
      var num1 = compressors(List(j, 1))
      var num2 = compressors(List(j-1, 0))
      var num3 = compressors(List(j-1, 1))
      resi(j) = cnt(j) + num2 + num3 + num0 + num1 - 2*num0 - 3*num1
      if (resi(j) < 0 || resi(j) > 2) {
        println("Wrong Compressors Structure! and column = " + j + " and res=" + resi(j))
      } 
    }

    for (j <- 0 until (n+m)) {
      if (resi(j) == 1) {
        res += j -> List(pos(j)-1, -1)
      }
      else if (resi(j) == 2) {
        res += j -> List(pos(j)-2, pos(j)-1)
      } else {
        println("Wrong Compressors Results! and column = " + j)
      }
    }
    res
  }
  
}