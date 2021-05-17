package wallace

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class HalfAdder extends Module {
  val io  = IO(new Bundle {
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val s = Output(UInt(1.W))
    val co = Output(UInt(1.W))
    })

  io.s := io.a ^ io.b
  io.co := io.a & io.b
}

class FullAdder extends Module {
  val io  = IO(new Bundle {
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val ci = Input(UInt(1.W))
    val s = Output(UInt(1.W))
    val co = Output(UInt(1.W))
    })

  val a_xor_b = io.a ^ io.b
  io.s := a_xor_b ^ io.ci

  val a_and_b = io.a & io.b
  val a_and_cin = io.a & io.ci
  val b_and_cin = io.b & io.ci
  io.co := a_and_b | b_and_cin | a_and_cin
}
