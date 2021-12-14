package basic

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class Compressor42 extends Module {
  val io  = IO(new Bundle {
    val I = Input(UInt(5.W))
    val O = Output(UInt(3.W))
    })

  val xor1 = io.I(0) ^ io.I(1)
  val xor2 = io.I(2) ^ io.I(3)
  val xor3 = xor1 ^ xor2
  io.O(0) := Mux(xor1, io.I(0), io.I(2))
  io.O(1) := xor3 ^ io.I(4)
  io.O(2) := Mux(xor3, io.I(3), io.I(4))
}