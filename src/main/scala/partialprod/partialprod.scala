package partialprod

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class PartialProd(m: Int, n: Int) extends Module {
  val io = IO(new Bundle {
    val multiplicand = Input(UInt(m.W))
    val multiplier = Input(UInt(n.W))
    val outs = Output(Vec(n, UInt(m.W)))
  })
  for (i <- 0 until n) {
    val tmp = (0 until m).map(j => Wire(UInt(1.W)))
    for (j <- 0 until m) {
      tmp(j) := io.multiplicand(j) & io.multiplier(i)
    }
    io.outs(i) := tmp.reverse.reduce(Cat(_, _))
  }
}

/*
object test{
  def main(args: Array[String]): Unit = {

    val topDesign = () => new PartialProd(4,3)
    chisel3.Driver.execute(Array("-td", "./RTL/partial"), topDesign)
    
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new PartialProdTester(c)
    }
    
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), () => new PartialProd(4,3)) {
      c => new PartialProdTester(c)
    }
  }
}

class PartialProdTester(c: PartialProd) extends PeekPokeTester(c) {
  poke(c.io.multiplicand, 5)
  poke(c.io.multiplier, 2)
  
  step(1)
  println("The multiplicand is: " + peek(c.io.multiplicand).toString())

  println("The result of 5 * 2 with is: " + peek(c.io.outs(0)).toString())

  expect(c.io.outs(0), 0)
  expect(c.io.outs(1), 5)
  expect(c.io.outs(2), 0)
}*/