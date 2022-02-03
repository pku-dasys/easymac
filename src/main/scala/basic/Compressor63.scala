package basic

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class Compressor63 extends Module {
  val io = IO(new Bundle {
    val I = Input(UInt(6.W))
    val O = Output(UInt(3.W))
  })

  val fa1 = Module(new FullAdder())
  fa1.io.A := io.I(1)
  fa1.io.B := io.I(2)
  fa1.io.Cin := io.I(3)
  val fs1 = fa1.io.Sum
  val fc1 = fa1.io.Cout

  val ha1 = Module(new HalfAdder())
  ha1.io.A := io.I(4)
  ha1.io.B := io.I(5)
  val hs1 = ha1.io.Sum
  val hc1 = ha1.io.Cout

  val fa2 = Module(new FullAdder())
  fa2.io.A := io.I(0)
  fa2.io.B := fs1
  fa2.io.Cin := hs1
  io.O(0) := fa2.io.Sum
  val fc2 = fa2.io.Cout

  val fa3 = Module(new FullAdder())
  fa3.io.A := fc2
  fa3.io.B := fc1
  fa3.io.Cin := hc1
  io.O(1) := fa3.io.Sum
  io.O(2) := fa3.io.Cout

}