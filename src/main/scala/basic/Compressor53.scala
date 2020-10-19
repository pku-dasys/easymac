package basic

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class Compressor53 extends Module {
  val io  = IO(new Bundle {
    val I = Input(UInt(5.W))
    val O = Output(UInt(3.W))
    })

  val fa1 = Module(new FullAdder())
  fa1.io.A := io.I(0)
  fa1.io.B := io.I(1)
  fa1.io.Cin := io.I(2) 
  io.O(0) := fa1.io.Sum
  val fc1 = fa1.io.Cout

  val fa2 = Module(new FullAdder())
  fa2.io.A := fc1
  fa2.io.B := io.I(3)
  fa2.io.Cin := io.I(4)
  io.O(1) := fa2.io.Sum
  io.O(2) := fa2.io.Cout

}