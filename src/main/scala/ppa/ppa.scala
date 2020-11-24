package ppa

import booth._
import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}
//import chisel3.{Bundle, _}

class PPAAnd(m:Int, n:Int) extends Module {
  val io = IO(new Bundle {
    val multiplicand = Input(UInt(m.W))
    val multiplier = Input(UInt(n.W))
    val outs = Output(Vec(n, UInt(m.W)))
  })
  
  val wire = Wire(UInt(m.W))
  for (i <- 0 until n) {
    when (io.multiplier(i) === 0.U(1.W)) {
      io.outs(i) := 0.U(m.W)
      printf("ppa 1 = %d\n", io.outs(i))
    }
    .otherwise {
      io.outs(i) := io.multiplicand
      printf("ppa 2 = %d\n", io.outs(i))
    }
    //io.outs(i) := wire
  }
}

object test{
  def main(args: Array[String]): Unit = {
    val topDesign = () => new PPAAnd(5, 4)
    chisel3.Driver.execute(Array("-td", "./RTL/ppa"), topDesign)
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new PPATester(c)
    }

    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), () => new PPAAnd(5, 4)) {
      c => new PPATester(c)
    }
  }
}

class PPATester(c: PPA) extends PeekPokeTester(c) {
  poke(c.io.multiplicand, 10)
  poke(c.io.multiplier, 4)
  
  step(1)
  println("The result of ppa multiplier is: " + peek(c.io.multiplier).toString())
  println("The result of ppa outs(0) is: " + peek(c.io.outs(0)).toString())
  println("The result of ppa outs(1) is: " + peek(c.io.outs(1)).toString())
}


