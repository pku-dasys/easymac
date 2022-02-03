package basic

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class FullAdder extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(1.W))
    val B = Input(UInt(1.W))
    val Cin = Input(UInt(1.W))
    val Sum = Output(UInt(1.W))
    val Cout = Output(UInt(1.W))
  })

  val a_xor_b = io.A ^ io.B
  io.Sum := a_xor_b ^ io.Cin

  val a_and_b = io.A & io.B
  val a_and_cin = io.A & io.Cin
  val b_and_cin = io.B & io.Cin
  io.Cout := a_and_b | b_and_cin | a_and_cin
}
