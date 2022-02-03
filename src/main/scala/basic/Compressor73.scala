package basic

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class Compressor73 extends Module {
  val io = IO(new Bundle {
    val I = Input(UInt(7.W))
    val O = Output(UInt(3.W))
  })

  val fa1 = Module(new FullAdder())
  fa1.io.A := io.I(1)
  fa1.io.B := io.I(2)
  fa1.io.Cin := io.I(3)
  val fs1 = fa1.io.Sum
  val fc1 = fa1.io.Cout

  val fa2 = Module(new FullAdder())
  fa2.io.A := io.I(4)
  fa2.io.B := io.I(5)
  fa2.io.Cin := io.I(6)
  val fs2 = fa2.io.Sum
  val fc2 = fa2.io.Cout

  val fa3 = Module(new FullAdder())
  fa3.io.A := io.I(0)
  fa3.io.B := fs1
  fa3.io.Cin := fs2
  io.O(0) := fa3.io.Sum
  val fc3 = fa3.io.Cout

  val fa4 = Module(new FullAdder())
  fa4.io.A := fc3
  fa4.io.B := fc1
  fa4.io.Cin := fc2
  io.O(1) := fa4.io.Sum
  io.O(2) := fa4.io.Cout

}