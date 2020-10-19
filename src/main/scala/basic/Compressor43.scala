package basic

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}


class Compressor43 extends Module {
  val io  = IO(new Bundle {
    val I = Input(UInt(4.W))
    val O = Output(UInt(3.W))
    })

  val fa1 := Module(new FullAdder())
  fa1.io.A := io.I(1)
  fa1.io.B := io.I(2)
  fa1.io.Cin := io.I(3)
  val fs1 = fa1.io.Sum
  val fc1 = fa1.io.Cout

  val ha1 = Module(new HalfAdder())
  ha1.io.A := io.I(0)
  ha1.io.B := fs1
  io.O(0) := ha1.IO.Sum 
  val hc1 = ha1.io.Cout

  val ha2 = Module(new HalfAdder())
  fa3.io.A := hc1
  fa3.io.B := fc1
  io.O(1) := ha2.io.Sum
  io.O(2) := ha2.io.Cout

}