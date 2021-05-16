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
  
  // get bits of an adder
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

  // get depth of a wallace tree
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

  /// @params[out]: key List[Int] = (column, depth, id), value List[Int] = (row, column)
  def getIn(m:Int, n:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
    val len = myarch.length
    var edgein = Map[List[Int], List[Int]]()

    // record the lst pos of each column
    var pos = Map[Int, Int]()

    // count the number of each column of each depth
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
      edgein += List(myarch(i), depth, cnt(myarch(i))) -> List(tmp, myarch(i))
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
    edgein
  }


  /// @params[out]: key List[int] = (column, depth, id), value List[Int] = (row of gen, column of gen, row of pro, column of pro)
  def getOut(m:Int, n:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
    val len = myarch.length
    var edgeout = Map[List[Int], List[Int]]()

    // record the lst pos of each column
    var pos = Map[Int, Int]()

    // count the number of each column of each depth
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

    println("abs = " + abs)
    println("min = " + min)

    for (i <- 0 until (n+m)) {
      if (i < min) {
        pos += i -> (i+1) 
      }
      else if (i >= min && i < (min + abs)) {
        pos += i -> (1 + min)
      } else {
        pos += i -> (n+m-i-1) 
      }
    }
    println("pos = " + pos )

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
      edgeout += List(myarch(i), depth, cnt(myarch(i))) -> List(tmp, myarch(i), tmp1, myarch(i)+1)
      pos += myarch(i) -> (tmp+1)
      pos += (myarch(i)+1) -> (tmp1+1)
      i += 2
    }
    edgeout
  }

  
  /// @params[out]: Int: i-bit List[Int] = (pos of addend, pos of augend) 
  def getRes(m:Int, n:Int, myarch:List[Int]) : Map[Int, List[Int]] = {
    val len = myarch.length
    var res = Map[Int, List[Int]]()
    // stat the compressors of each column List[Int] = {column, the type of compressors}
    var compressors = Map[List[Int], Int]()
    // record the lst pos of each column
    var pos = Map[Int, Int]()

    // count the number of each column of each depth
    var cnt = new Array[Int](256)

    // count the number of residual of each column
    var resi = new Array[Int](256)

    var abs = 0
    var min = 0
    if (m > n) {
      abs = m - n
      min = n
    } else {
      abs = n - m
      min = m
    }

    // initialize
    for (i <- 0 until (n+m)) {
      if (i < min) {
        pos += i -> (i+1) 
        cnt(i) = i+1
      }
      else if (i >= min && i < (min + abs)) {
        pos += i -> (1 + min)
        cnt(i) = 1+min
      } else {
        pos += i -> (n+m-i-1) 
        cnt(i) = n+m-i-1
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
    for (j <- 1 until (n+m)) {
      var num0 = compressors(List(j, 0))
      var num1 = compressors(List(j, 1))
      var num2 = compressors(List(j-1, 0))
      var num3 = compressors(List(j-1, 1))
      resi(j) = cnt(j) + num2 + num3 + num0 + num1 - 2*num0 - 3*num1
      assert(resi(j) >= 0 && resi(j) <= 2, "Wrong Compressors Structure!")    
    }

    for (j <- 0 until (n+m)) {
      if (resi(j) == 1) {
        res += j -> List(pos(j)-1, -1)
      }
      else if (resi(j) == 2) {
        res += j -> List(pos(j)-2, pos(j)-1)
      } else {
        println("Wrong Compressors Results!")
      }
    }
    res
  }
  
}