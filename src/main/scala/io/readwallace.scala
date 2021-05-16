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
  def getIn(n:Int, m:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
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


  /// @params[out]: key List[int] = (column, depth, id), value List[Int] = (row, column)
  
  def getG(n:Int, dep:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
    val len = myarch.length
    var gedge = Map[List[Int], List[Int]]()
    var isprefix = Map[List[Int], Int]()
  }

  /*
  /// @params[out]: key List[int] = (column, depth, id), value List[Int] = (row, column)
  def getP(n:Int, dep:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {

  }*/
  
}