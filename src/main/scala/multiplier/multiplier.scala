package multiplier

import basic._
import adder._
import ppa._

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}


class Multiplier(m:Int, n:Int) extends Module{
  val io = IO(new Bundle {
    val multiplicand = Input(UInt(m.W))
    val multiplier = Input(UInt(n.W))
    val res = Output(UInt((m+n).W))
    })
  
  
  val ppa = Module(new PPA(m, n))

  ppa.io.multiplicand := io.multiplicand

  ppa.io.multiplier := io.multiplier

  printf("ppa.io.multiplier = %d\n",  ppa.io.multiplier)
  printf("ppa.io.multiplicand = %d\n",  ppa.io.multiplicand)

  val wire1 = ppa.io.outs

  printf("wire1(0) = %d\n",  wire1(0))
  printf("wire1(1) = %d\n",  wire1(1))
  
  val sum = Wire(UInt((m+n).W))
  for (i <- 1 until n) {
    val adder = Module(new BasicAdder(m+i+1))
    adder.io.sumand := wire1(i-1)
    adder.io.addend := wire1(i) << i
    //printf("adder.io.addend = %d", adder.io.addend)
    //printf("adder.io.sumand = %d", adder.io.sumand)
    sum := adder.io.res
  }

  io.res := sum
}

/*
object test{
  def main(args: Array[String]): Unit = {
    val topDesign = () => new Multiplier(5, 3)
    chisel3.Driver.execute(Array("-td", "./RTL/multiplier"), topDesign)
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new MultiplierTester(c)
    }

    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), () => new Multiplier(5, 3)) {
      c => new MultiplierTester(c)
    }
  }
}

class MultiplierTester(c: Multiplier) extends PeekPokeTester(c) {
  poke(c.io.multiplicand, 3)
  poke(c.io.multiplier, 2)
  
  step(1)
  println("The result of ppa multiplier is: " + peek(c.ppa.io.multiplier).toString())
  println("The result of ppa outs(0) is: " + peek(c.ppa.io.outs(0)).toString())
  println("The result of ppa outs(1) is: " + peek(c.ppa.io.outs(1)).toString())
  println("The result of 3 * 2 with is: " + peek(c.io.res).toString())

  expect(c.io.res, 6)
  expect(c.ppa.io.outs(0), 0)
  expect(c.ppa.io.outs(1), 3)
}*/
