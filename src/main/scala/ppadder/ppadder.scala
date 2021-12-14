package ppadder

import io._
import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}
//import chisel3.{Bundle, _}

import java.io.PrintWriter
import java.io.File

import scala.io.Source

//import collection.mutable.Map
/*
class PPAdder(n:Int) extends Module {
  
  val io = IO(new Bundle {
    val augend = Input(UInt(n.W))
    val addend = Input(UInt(n.W))
    val outs = Output(UInt(n.W))
  })
  io.outs := io.addend + io.augend
}*/

class PPAdder(n:Int, myarch:List[Int], pedge:Map[List[Int], List[Int]], gedge:Map[List[Int], List[Int]], post:Map[Int, Int]) extends Module {
  val io = IO(new Bundle {
    val augend = Input(UInt(n.W))
    val addend = Input(UInt(n.W))
    val outs = Output(UInt(n.W))
  })
  
  // pre-computations

  //val debugg = (0 until n).map(i => Wire(UInt(1.W))) // debug
  //val debugp = (0 until n).map(i => Wire(UInt(1.W))) // debug
  var tmpl = Map[Int, Int]()
  var tmplev = Map[Int, Int]()

  // initialize
  for (i <- 0 until n) {
    tmpl += i -> i
    tmplev += i -> -1
  }

  var GMap = Map[List[Int], Data]()
  var PMap = Map[List[Int], Data]()
  for (i <- 0 until n) {
    val pg = Module(new PG)
    pg.io.i_a := io.augend(i)
    pg.io.i_b := io.addend(i)
    GMap += List(-1, i) -> pg.io.o_g
    PMap += List(-1, i) -> pg.io.o_p
    //debugg(i) := GMap(List(-1, i))
    //debugp(i) := PMap(List(-1, i))
  }
  // prefix graph
  val len = myarch.length
  val len1 = pedge.size
  val len2 = gedge.size
  assert(len == len1, "Wrong Parse Results")
  assert(len == len2, "Wrong Parse Results")
  assert(len1 == len2, "Wrong Parse Results")

  for (i <- 0 until len1) {
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
    //println("debug: " + depth + " " + myarch(i))
    val black = Module(new Black)
    black.io.i_gk := GMap(gedge(List(d3, x)))
    black.io.i_pk := PMap(gedge(List(d3, x)))
    black.io.i_gj := GMap(pedge(List(d3, x)))
    black.io.i_pj := PMap(pedge(List(d3, x)))

    GMap += List(d3, x) -> black.io.o_g
    PMap += List(d3, x) -> black.io.o_p
  }

  // post computation
  val carry = (0 until n).map(i => GMap(List(post(i), i)).asUInt())

  val res = (0 until n).map(i => Wire(UInt(1.W)))
  res(0) := PMap(List(-1, 0)).asUInt() ^ 0.U
  printf(p"res(0) = ${res(0)}\n")

  //val tmp = 0.U(1.W)

  //val res0 = PMap(List(-1, 0)).asUInt() ^ tmp

  //io.outs(0) := PMap(List(-1, 0)).asUInt()

  //var res = (1 until n).map(i => (PMap(List(-1, n-i)).asUInt()^carry(n-i-1)))
  //res = Cat(res, PMap(List(-1, 0)).asUInt() ^ 0.U)
  
  for (i <- 1 until n) {
    val resi = PMap(List(-1, i)).asUInt() ^ carry(i-1).asUInt()
    res(i) := resi.asUInt()
    //printf(p"res(${i}) = ${res(i)}\n")
  }

  io.outs := res.reverse.reduce(Cat(_,_))
  //io.outs := res
}

/*
object test{
  val usage = """
      Usage: generate [--input1-bit input1] [--input2-bit input2] [--compressor-file filename1] [--prefix-adder-file filename2]
  """
  def main(args: Array[String]): Unit = {
    
    if (args.length == 0) println(usage)
    
    val arglist = args.toList
    val optionNames = arglist.filter(s => s.contains('-'))

    //type OptionMap = Map[Symbol, Any]

    val argmap = (0 until arglist.size / 2).map(i => arglist(i * 2) -> arglist(i * 2 + 1)).toMap

    val filename1 = argmap("--prefix-adder-file")

    println(filename1)
    val filecontent = ReadPPA.readFromPPATxt(filename1)

    val n = ReadPPA.getBits(filecontent)(0)
    println("A " + n + "-bit parallel prefix adder")

    val numcells = ReadPPA.getNumCells(filecontent)(0)
    println("The prefix nodes are: " + numcells)

    val myarch = ReadPPA.getArch(filecontent)
    println(myarch)

    val dep = ReadPPA.getDepth(myarch)
    println("The depth is: " + dep)

    val pedge = ReadPPA.genPEdge(n, dep, myarch)
    println(pedge)

    val gedge = ReadPPA.genGEdge(n, dep, myarch)
    println(gedge)

    val pos = ReadPPA.genFinal(n, myarch)
    println(pos)

    val topDesign = () => new PPAdder(n, myarch, pedge, gedge, pos)
    chisel3.Driver.execute(Array("-td", "./RTL/ppadder"), topDesign)
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new PPAdderTester(c)
    }

    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), () => new PPAdder(n, myarch, pedge, gedge, pos)) {
      c => new PPAdderTester(c)
    }
  }
}

class PPAdderTester(c: PPAdder) extends PeekPokeTester(c) {
  poke(c.io.augend, 12)
  poke(c.io.addend, 54)
  
  step(1)
  println("The addend of parallel prefix adder is: " + peek(c.io.addend).toString())
  println("The result of parallel prefix adder is: " + peek(c.io.outs).toString())

  println("The result of 12 + 54 with is: " + peek(c.io.outs).toString())

  expect(c.io.outs, 66)
}*/
