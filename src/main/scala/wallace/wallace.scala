package wallace

import io._
import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}
//import chisel3.{Bundle, _}

import java.io.PrintWriter
import java.io.File

import scala.io.Source

class PPAdder(n:Int) extends Module {
  
  val io = IO(new Bundle {
    val augend = Input(UInt(n.W))
    val addend = Input(UInt(n.W))
    val outs = Output(UInt(n.W))
  })
  io.outs := io.addend + io.augend
}
/*
class Wallace(n:Int) extends Module {
  val io = IO(new Bundle {
    val pp = Input(Vec(n, UInt(m.W))
    val augend = Output(UInt((n+m).W))
    val addend = Outpus(UInt((n+m).W))
  })
  
  var ValueMap = Map[List[Int], Data]

  for (i <- 0 until n) {
    for (j <- 0 until m) {
      ValueMap += List(i, j+i) -> io.pp(i)(j)
    }
  }

  
}*/

object test{
  val usage = """
      Usage: readwt [--wallace-file filename1]
  """
  def main(args: Array[String]): Unit = {
    
    if (args.length == 0) println(usage)
    
    val arglist = args.toList
    val optionNames = arglist.filter(s => s.contains('-'))

    val argmap = (0 until arglist.size / 2).map(i => arglist(i * 2) -> arglist(i * 2 + 1)).toMap

    val filename1 = argmap("--wallace-file")

    println(filename1)
    val filecontent = ReadWT.readFromWTTxt(filename1)

    val m = ReadWT.getBits(filecontent)(0)
    val n = ReadWT.getBits(filecontent)(1)
    println("A " + m + "*" + n + "-bit compressor tree")

    val numcells = ReadWT.getNumCells(filecontent)(0)
    println("The compressors are: " + numcells)

    val myarch = ReadWT.getArch(filecontent)
    println(myarch)

    val depth = ReadWT.getDepth(myarch)
    println(depth)

    val inedges = ReadWT.getIn(m, n, myarch)
    println(inedges)

    val topDesign = () => new PPAdder(8)
    chisel3.Driver.execute(Array("-td", "./RTL/wt"), topDesign)
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new PPAdderTester(c)
    }

    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), () => new PPAdder(8)) {
      c => new PPAdderTester(c)
    }
  }
}

class PPAdderTester(c: PPAdder) extends PeekPokeTester(c) {
  poke(c.io.augend, 1)
  poke(c.io.addend, 5)
  
  step(1)
  println("The addend of parallel prefix adder is: " + peek(c.io.addend).toString())
  println("The result of parallel prefix adder is: " + peek(c.io.outs).toString())

  println("The result of 1 + 5 with is: " + peek(c.io.outs).toString())

  expect(c.io.outs, 6)
}