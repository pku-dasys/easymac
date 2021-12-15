package io

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

import scala.io.Source
import scala.util.control._

object ReadPPA {
  def readFromPPATxt(filePath:String) = {
    val source = Source.fromFile(filePath, "UTF-8")

    val lines = source.getLines().toArray
    source.close
    lines
  }
  
  // get bits of an adder
  def getBits(array:Array[String]) : List[Int] = {
    val inputbits = array(0).trim.split(" ")
    val res = inputbits(0).toInt :: Nil
    res
  }

  // get the number of prefix cells
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

  // get depth of a prefix graph
  def getDepth(myarch:List[Int]) : Int = {
    val len = myarch.length
    var dep = 1
    var ind = 500
    for (i <- 0 until len) {
      if (myarch(i) >= ind) {
        dep = dep + 1
      }
      ind = myarch(i)
    }
    dep
  }

  
  def genPEdge(n:Int, dep:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
    val len = myarch.length
    var pedge = Map[List[Int], List[Int]]()

    var tmpl = Map[Int, Int]()
    var tmplev = Map[Int, Int]()

    // initialize
    for (i <- 0 until n) {
      tmpl += i -> i
      tmplev += i -> -1
    }

    for (i <- 0 until len) {
      var x=myarch(i)
      var y=tmpl(myarch(i))-1
      var d1 = tmplev(x)
      var d2 = tmplev(y)
      var d3 = 0
      if (d1 > d2) {
        d3 = d1 + 1
      } else {
        d3 = d2 + 1
      }
      tmplev += myarch(i) -> d3
      tmpl += myarch(i) -> tmpl(y)
      pedge += List(d3, x) -> List(d2, y)
    }
    pedge
  }

  def genGEdge(n:Int, dep:Int, myarch:List[Int]) : Map[List[Int], List[Int]] = {
    val len = myarch.length
    var gedge = Map[List[Int], List[Int]]()
    var isprefix = Map[List[Int], Int]()

    var tmpl = Map[Int, Int]()
    var tmplev = Map[Int, Int]()

    // initialize
    for (i <- 0 until n) {
      tmpl += i -> i
      tmplev += i -> -1
    }
    for (i <- 0 until dep) {
      for (j <- -1 until n) {
        isprefix += List(i, j) -> 0
      }
    }

    for (i <- 0 until len) {
      var x=myarch(i)
      var y=tmpl(myarch(i))-1
      var d1 = tmplev(x)
      var d2 = tmplev(y)
      var d3 = 0
      if (d1 > d2) {
        d3 = d1 + 1
      } else {
        d3 = d2 + 1
      }
      tmplev += myarch(i) -> d3
      tmpl += myarch(i) -> tmpl(y)
      gedge += List(d3, x) -> List(d1, x)
    }
    gedge
  }

  def genFinal(n:Int, myarch:List[Int]) : Map[Int, Int] = {
    val len = myarch.length
    var res = Map[Int, Int]()
    for (i <- 0 until n) {
      res += i -> -1
    }

    var tmpl = Map[Int, Int]()
    var tmplev = Map[Int, Int]()

    // initialize
    for (i <- 0 until n) {
      tmpl += i -> i
      tmplev += i -> -1
    }

    for (i <- 0 until len) {
      var x=myarch(i)
      var y=tmpl(myarch(i))-1
      var d1 = tmplev(x)
      var d2 = tmplev(y)
      var d3 = 0
      if (d1 > d2) {
        d3 = d1 + 1
      } else {
        d3 = d2 + 1
      }
      tmplev += myarch(i) -> d3
      tmpl += myarch(i) -> tmpl(y)
      res += x -> d3
    }
    res
  }
  
}