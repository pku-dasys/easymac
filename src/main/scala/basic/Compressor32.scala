package basic

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class Compressor32 extends Module {
  val io = IO(new Bundle {
    val I = Input(UInt(3.W))
    val O = Output(UInt(2.W))
  })

  val xor1 = io.I(0) ^ io.I(1)
  io.O(0) := Mux(xor1, io.I(0), io.I(1))
  //val mux1 = Module(new Mux(xor1, io.io.I(0), io.I(1)))
  io.O(1) := xor1 ^ io.I(2)

}