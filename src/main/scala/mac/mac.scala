package mac

import io._
import ppadder._
import partialprod._
import wallace._
import wtmultiplier._

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

import java.io.PrintWriter
import java.io.File

import scala.io.Source


class MAC(m:Int, n:Int, myarchw:List[Int], inedges:Map[List[Int], List[Int]], outedges:Map[List[Int], List[Int]], res:Map[Int, List[Int]], 
	               myarcha:List[Int], pedge:Map[List[Int], List[Int]], gedge:Map[List[Int], List[Int]], post:Map[Int, Int],
	               myarcha2:List[Int], pedge2:Map[List[Int], List[Int]], gedge2:Map[List[Int], List[Int]], post2:Map[Int, Int]) extends Module {
  val io = IO(new Bundle {
    val multiplicand = Input(UInt(m.W))
    val multiplier = Input(UInt(n.W))
    val addend = Input(UInt((m+n-1).W))
    val outs = Output(UInt((m+n-1).W))
  })

  val pp = Module(new PartialProd(m, n))
  pp.io.multiplicand := io.multiplicand
  pp.io.multiplier := io.multiplier

  val wt = Module(new Wallace(m, n, myarchw, inedges, outedges, res))
  wt.io.pp := pp.io.outs

  val ppa1 = Module(new PPAdder((m+n-1), myarcha, pedge, gedge, post))
  ppa1.io.augend := wt.io.augend
  ppa1.io.addend := wt.io.addend

  val ppa2 = Module(new PPAdder((m+n-1), myarcha2, pedge2, gedge2, post2))
  ppa2.io.augend := ppa1.io.outs
  ppa2.io.addend := io.addend

  io.outs := ppa2.io.outs
}

class MAC1(m:Int, n:Int, myarchw:List[Int], inedges:Map[List[Int], List[Int]], outedges:Map[List[Int], List[Int]], res:Map[Int, List[Int]], 
	               myarcha:List[Int], pedge:Map[List[Int], List[Int]], gedge:Map[List[Int], List[Int]], post:Map[Int, Int],
	               myarcha2:List[Int], pedge2:Map[List[Int], List[Int]], gedge2:Map[List[Int], List[Int]], post2:Map[Int, Int]) extends Module {
  val io = IO(new Bundle {
    val multiplicand = Input(UInt(m.W))
    val multiplier = Input(UInt(n.W))
    val addend = Input(UInt((m+n-1).W))
    val outs = Output(UInt((m+n-1).W))
  })

  val wtm = Module(new WTMultiplier(m, n, myarchw, inedges, outedges, res, myarcha, pedge, gedge, post))
  wtm.io.multiplicand := io.multiplicand
  wtm.io.multiplier := io.multiplier

  val ppa = Module(new PPAdder((m+n-1), myarcha2, pedge2, gedge2, post2))
  ppa.io.augend := wtm.io.outs
  ppa.io.addend := io.addend

  io.outs := ppa.io.outs
}

class MAC2(m:Int, n:Int, myarchw:List[Int], inedges:Map[List[Int], List[Int]], outedges:Map[List[Int], List[Int]], res:Map[Int, List[Int]], 
	               myarcha:List[Int], pedge:Map[List[Int], List[Int]], gedge:Map[List[Int], List[Int]], post:Map[Int, Int]) extends Module {
  val io = IO(new Bundle {
    val multiplicand = Input(UInt(m.W))
    val multiplier = Input(UInt(n.W))
    val addend = Input(UInt((m+n-1).W))
    val outs = Output(UInt((m+n-1).W))
  })

  val pp = Module(new PartialProd(m, n))
  pp.io.multiplicand := io.multiplicand
  pp.io.multiplier := io.multiplier

  val wt = Module(new Wallace1(m, n, myarchw, inedges, outedges, res))
  wt.io.pp := pp.io.outs
  wt.io.accmulatend := io.addend

  val ppa = Module(new PPAdder((m+n), myarcha, pedge, gedge, post))
  ppa.io.augend := wt.io.augend
  ppa.io.addend := wt.io.addend

  io.outs := ppa.io.outs
}


object test{
  val usage = """
      Usage: generate [--compressor-file filename1] [--prefix-adder-file filename2] [--accmulation-file filename3]
  """
  def main(args: Array[String]): Unit = {
    
    if (args.length == 0) println(usage)
    
    val arglist = args.toList

    //type OptionMap = Map[Symbol, Any]

    val argmap = (0 until arglist.size / 2).map(i => arglist(i * 2) -> arglist(i * 2 + 1)).toMap

    val filename1 = argmap("--wallace-file")
    val filename2 = argmap("--prefix-adder-file")
    val filename3 = argmap("--accmulation-file")

    println(filename1)
    println(filename2)
    println(filename3)

    val filecontent = ReadWT.readFromWTTxt(filename1)

    val m = ReadWT.getBits(filecontent)(0)
    val n = ReadWT.getBits(filecontent)(1)

    // wallace tree
    val numcompressors = ReadWT.getNumCells(filecontent)(0)
    println("The compressors are: " + numcompressors)

    val myarchw = ReadWT.getArch(filecontent)
    println(myarchw)

    val depthw = ReadWT.getDepth(myarchw)
    println(depthw)

    val inedges = ReadWT.getIn(m, n, myarchw)
    println(inedges)

    val outedges = ReadWT.getOut(m, n, myarchw)
    println(outedges)

    val res = ReadWT.getRes(m, n, myarchw)
    println(res)

    //final adder
    val filecontent1 = ReadPPA.readFromPPATxt(filename2)

    val l = ReadPPA.getBits(filecontent1)(0)
    println("A " + n + "-bit parallel prefix adder")

    val numpgcells = ReadPPA.getNumCells(filecontent1)(0)
    println("The prefix nodes are: " + numpgcells)

    val myarcha = ReadPPA.getArch(filecontent1)
    println(myarcha)

    val deptha = ReadPPA.getDepth(myarcha)
    println("The depth of ppa is: " + deptha)

    val pedge = ReadPPA.genPEdge(l, deptha, myarcha)
    println(pedge)

    val gedge = ReadPPA.genGEdge(l, deptha, myarcha)
    println(gedge)

    val pos = ReadPPA.genFinal(l, myarcha)
    println(pos)

    //accumulate adder
    val filecontent2 = ReadPPA.readFromPPATxt(filename2)

    val l2 = ReadPPA.getBits(filecontent2)(0)
    println("A " + n + "-bit parallel prefix adder")

    val numpgcells2 = ReadPPA.getNumCells(filecontent1)(0)
    println("The prefix nodes are: " + numpgcells)

    val myarcha2 = ReadPPA.getArch(filecontent1)
    println(myarcha2)

    val deptha2 = ReadPPA.getDepth(myarcha)
    println("The depth of ppa2 is: " + deptha2)

    val pedge2 = ReadPPA.genPEdge(l, deptha, myarcha)
    println(pedge2)

    val gedge2 = ReadPPA.genGEdge(l, deptha, myarcha)
    println(gedge2)

    val pos2 = ReadPPA.genFinal(l, myarcha)
    println(pos2)

    val topDesign = () => new MAC(m, n, myarchw, inedges, outedges, res, myarcha, pedge, gedge, pos, myarcha2, pedge2, gedge2, pos2)
    chisel3.Driver.execute(Array("-td", "./RTL/mac"), topDesign)
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new MACTester(c)
    }

    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), () => new MAC(m, n, myarchw, inedges, outedges, res, myarcha, pedge, gedge, pos, myarcha2, pedge2, gedge2, pos2)) {
      c => new MACTester(c)
    }
  }
}
/*
object test{
  val usage = """
      Usage: generate [--compressor-file filename1] [--prefix-adder-file filename2]
  """
  def main(args: Array[String]): Unit = {
    
    if (args.length == 0) println(usage)
    
    val arglist = args.toList

    //type OptionMap = Map[Symbol, Any]

    val argmap = (0 until arglist.size / 2).map(i => arglist(i * 2) -> arglist(i * 2 + 1)).toMap

    val filename1 = argmap("--wallace-file")
    val filename2 = argmap("--prefix-adder-file")

    println(filename1)
    println(filename2)

    val filecontent = ReadWT.readFromWTTxt(filename1)

    val m = ReadWT.getBits(filecontent)(0)
    val n = ReadWT.getBits(filecontent)(1)

    // wallace tree
    val numcompressors = ReadWT.getNumCells(filecontent)(0)
    println("The compressors are: " + numcompressors)

    val myarchw = ReadWT.getArch(filecontent)
    println(myarchw)

    val depthw = ReadWT.getDepth(myarchw)
    println(depthw)

    val inedges = ReadWT.getIn1(m, n, myarchw)
    println(inedges)

    val outedges = ReadWT.getOut1(m, n, myarchw)
    println(outedges)

    val res = ReadWT.getRes1(m, n, myarchw)
    println("wt res = " + res)

    //final adder
    val filecontent1 = ReadPPA.readFromPPATxt(filename2)

    val l = ReadPPA.getBits(filecontent1)(0)
    println("A " + n + "-bit parallel prefix adder")

    val numpgcells = ReadPPA.getNumCells(filecontent1)(0)
    println("The prefix nodes are: " + numpgcells)

    val myarcha = ReadPPA.getArch(filecontent1)
    println(myarcha)

    val deptha = ReadPPA.getDepth(myarcha)
    println("The depth of ppa is: " + deptha)

    val pedge = ReadPPA.genPEdge(l, deptha, myarcha)
    println(pedge)

    val gedge = ReadPPA.genGEdge(l, deptha, myarcha)
    println(gedge)

    val pos = ReadPPA.genFinal(l, myarcha)
    println(pos)

    val topDesign = () => new MAC2(m, n, myarchw, inedges, outedges, res, myarcha, pedge, gedge, pos)
    chisel3.Driver.execute(Array("-td", "./RTL/mult"), topDesign)
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new MACTester(c)
    }

    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), () => new MAC2(m, n, myarchw, inedges, outedges, res, myarcha, pedge, gedge, pos)) {
      c => new MACTester(c)
    }
  }
}*/



class MACTester(c: MAC) extends PeekPokeTester(c) {
  poke(c.io.multiplicand, 7)
  poke(c.io.multiplier, 2)
  poke(c.io.addend, 1)
  
  step(1)

  println("The result of 7 * 2 + 1 is: " + peek(c.io.outs).toString())

  expect(c.io.outs, 15)
}