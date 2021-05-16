package multiplier

import io._
import ppadder._
import partial._
import wallace._

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

import java.io.PrintWriter
import java.io.File

import scala.io.Source

/*
class WTMultiplier(m:Int, n:Int) extends Module {
  val io = IO(new Bundle {
    val multiplicand = Input(UInt(m.W))
    val multiplier = Input(UInt(n.W))
    val outs = Output(UInt((m+n).W))
  })
  
}

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
  poke(c.io.augend, 12345)
  poke(c.io.addend, 54321)
  
  step(1)
  println("The addend of parallel prefix adder is: " + peek(c.io.addend).toString())
  println("The result of parallel prefix adder is: " + peek(c.io.outs).toString())

  println("The result of 12345 + 54321 with is: " + peek(c.io.outs).toString())

  expect(c.io.outs, 66666)
}*/