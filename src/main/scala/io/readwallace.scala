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

  
  def genPEdge(n:Int, dep:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
    val len = myarch.length
    //var vis = (0 until n).map(i => 0).toList
    var pedge = Map[List[Int], List[Int]]()
    var isprefix = Map[List[Int], Int]()

    // initialize isprefix
    for (i <- 0 until n) {
      isprefix += List(-1, i) -> 1
    }
    for (i <- 0 until dep) {
      for (j <- -1 until n) {
        isprefix += List(i, j) -> 0
      }
    }

    var depth = 0
    var ind_dep = 500
    for (i <- 0 until len) {
      if (myarch(i) > ind_dep) {
        depth = depth + 1
      }
      var flag = true
      var tmpd = depth
      while (flag == true) {
        var ind_pre = myarch(i)
        while (ind_pre >= 0 && flag == true) {
          //println("debug: " + myarch(i) + " " + depth)
          //println("find:" + (tmpd-1).toString + " " + (ind_pre-1).toString)
          if (isprefix(List(tmpd-1, ind_pre-1)) == 1) {
            //println("ddebug: " + List(tmpd-1, ind_pre-1))
            pedge += List(depth, myarch(i)) -> List(tmpd-1, ind_pre-1)
            isprefix += List(depth, myarch(i)) -> 1
            flag = false
          } else {
            ind_pre -= 1
          }
        }
        tmpd -= 1
      }
      ind_dep = myarch(i)
    }
    pedge
  }

  def genGEdge(n:Int, dep:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
    val len = myarch.length
    var gedge = Map[List[Int], List[Int]]()
    var isprefix = Map[List[Int], Int]()

    // initialize isprefix
    for (i <- 0 until n) {
      isprefix += List(-1, i) -> 1
    }
    for (i <- 0 until dep) {
      for (j <- -1 until n) {
        isprefix += List(i, j) -> 0
      }
    }
    var depth = 0
    var ind_dep = 500
    for (i <- 0 until len) {
      //println("debug: " + myarch(i) + " " + depth)
      if (myarch(i) > ind_dep) {
        depth = depth + 1
      }
      var flag = true
      var ind = depth-1
      while (flag == true && ind >= -1) {
        if (isprefix(List(ind, myarch(i))) == 1) {
          //println("ddebug: " + List(ind, myarch(i)))
          gedge += List(depth, myarch(i)) -> List(ind, myarch(i))
          isprefix +=List(depth, myarch(i)) -> 1
          flag = false
        } else {
          ind -= 1
        }
      }
      ind_dep = myarch(i)
    }
    gedge
  }

  def genFinal(n:Int, myarch:List[Int]) : Map[Int, Int] = {
    var res = Map[Int, Int]()
    for (i <- 0 until n) {
      res += i -> -1
    }

    val len = myarch.length
    var ind_dep = 500
    var depth = 0
    for (i <- 0 until len) {
      if (myarch(i) > ind_dep) {
        depth = depth + 1
      }
      res += myarch(i) -> depth
      ind_dep = myarch(i)
    }
    res
  }
  
}