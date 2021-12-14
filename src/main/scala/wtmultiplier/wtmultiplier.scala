package wtmultiplier

import io._
import ppadder._
import partialprod._
import wallace._

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

import java.io.PrintWriter
import java.io.File

import scala.io.Source


class WTMultiplier(m:Int, n:Int, myarchw:List[Int], inedges:Map[List[Int], List[Int]], outedges:Map[List[Int], List[Int]], res:Map[Int, List[Int]], myarcha:List[Int], pedge:Map[List[Int], List[Int]], gedge:Map[List[Int], List[Int]], post:Map[Int, Int]) extends Module {
  val io = IO(new Bundle {
    val multiplicand = Input(UInt(m.W))
    val multiplier = Input(UInt(n.W))
    val outs = Output(UInt((m+n-1).W))
  })

  val pp = Module(new PartialProd(m, n))
  pp.io.multiplicand := io.multiplicand
  pp.io.multiplier := io.multiplier

  val wt = Module(new Wallace(m, n, myarchw, inedges, outedges, res))
  wt.io.pp := pp.io.outs

  val ppa = Module(new PPAdder((m+n-1), myarcha, pedge, gedge, post))
  ppa.io.augend := wt.io.augend
  ppa.io.addend := wt.io.addend

  io.outs := ppa.io.outs
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

    val filename1 = argmap("--wallace-file")
    val filename2 = argmap("--prefix-adder-file")

    println(filename1)
    println(filename2)

    val filecontent = ReadWT.readFromWTTxt(filename1)

    val m = ReadWT.getBits(filecontent)(0)
    val n = ReadWT.getBits(filecontent)(1)

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

    val topDesign = () => new WTMultiplier(m, n, myarchw, inedges, outedges, res, myarcha, pedge, gedge, pos)
    chisel3.Driver.execute(Array("-td", "./RTL/mult"), topDesign)
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new WTMultiplierTester(c)
    }

    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), () => new WTMultiplier(m, n, myarchw, inedges, outedges, res, myarcha, pedge, gedge, pos)) {
      c => new WTMultiplierTester(c)
    }
  }
}

class WTMultiplierTester(c: WTMultiplier) extends PeekPokeTester(c) {
  poke(c.io.multiplicand, 15)
  poke(c.io.multiplier, 8)
  
  step(1)

  println("The result of 15 * 8 with is: " + peek(c.io.outs).toString())

  expect(c.io.outs, 120)
}*/