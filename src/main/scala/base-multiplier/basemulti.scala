package basemulti

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class BasicMultiplier(m: Int, n: Int) extends Module {
  val io = IO(new Bundle {
    val multiplicand = Input(UInt(m.W))
    val multiplier = Input(UInt(n.W))

    val res = Output(UInt((m + n).W))
  })
  io.res := io.multiplicand * io.multiplier
}

object test {
  def main(args: Array[String]): Unit = {
    val topDesign = () => new BasicMultiplier(16, 16)
    (new chisel3.stage.ChiselStage).emitVerilog(topDesign(), Array("-td", "./RTL/basicmulti"))
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new BasicMultiplierTester(c)
    }
  }
}

class BasicMultiplierTester(c: BasicMultiplier) extends PeekPokeTester(c) {
  poke(c.io.multiplicand, 15)
  poke(c.io.multiplier, 8)

  println("The result of 15 * 8 with is: " + peek(c.io.res).toString())

  expect(c.io.res, 120)
}
