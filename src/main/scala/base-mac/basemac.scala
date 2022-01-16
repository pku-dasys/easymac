package basemac

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class BasicMAC(m:Int, n:Int) extends Module{
  val  io = IO(new Bundle {
    val multiplicand = Input(UInt(m.W))
    val multiplier = Input(UInt(n.W))
    val addend = Input(UInt((m+n).W))

    val res = Output(UInt((m+n).W))
  })

  io.res := io.multiplicand * io.multiplier + io.addend
}

object test{
  val usage = """
  	Usage: generate [--input1-bit input1] [--input2-bit input2]
  """
  def main(args: Array[String]): Unit = {
  	if (args.length == 0) println(usage)

  	val arglist = args.toList
  	val optionnames = arglist.filter(s => s.contains('-'))

  	val argmap = (0 until arglist.size / 2).map(i => arglist(i * 2) -> arglist(i * 2 + 1)).toMap

  	val input1 = argmap("--input1-bit").toInt
  	val input2 = argmap("--input2-bit").toInt

  	val topDesign = () => new BasicMAC(input1, input2)
    (new chisel3.stage.ChiselStage).emitVerilog(topDesign(), Array("-td", "./RTL/basicmac"))
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new BasicMACTester(c)
    }

    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), () => new BasicMAC(input1, input2)) {
      c => new BasicMACTester(c)
    }
    
  }
}

class BasicMACTester(c: BasicMAC) extends PeekPokeTester(c) {
  poke(c.io.multiplicand, 15)
  poke(c.io.multiplier, 8)
  poke(c.io.addend, 10)

  println("The result of 15 * 8 + 10 is: " + peek(c.io.res).toString())

  expect(c.io.res, 130)
}
