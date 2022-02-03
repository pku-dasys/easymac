package ppadder

import chisel3.iotesters.PeekPokeTester
import chisel3.util._
import chisel3.{Bundle, Input, Module, Output, UInt, _}

class Grey extends Module {
  val io = IO(new Bundle {
    val i_gj = Input(UInt(1.W))
    val i_pk = Input(UInt(1.W))
    val i_gk = Input(UInt(1.W))
    val o_g = Output(UInt(1.W))
  })
  io.o_g := io.i_gk | (io.i_gj & io.i_pk);
}

class Black extends Module {
  val io = IO(new Bundle {
    val i_pj = Input(UInt(1.W))
    val i_gj = Input(UInt(1.W))
    val i_pk = Input(UInt(1.W))
    val i_gk = Input(UInt(1.W))
    val o_g = Output(UInt(1.W))
    val o_p = Output(UInt(1.W))
  })
  io.o_g := io.i_gk | (io.i_gj & io.i_pk)
  io.o_p := io.i_pk & io.i_pj
}

class Buffer extends Module {
  val io = IO(new Bundle {
    val i_p = Input(UInt(1.W))
    val i_g = Input(UInt(1.W))
    val o_p = Output(UInt(1.W))
    val o_g = Output(UInt(1.W))
  })
  io.o_p := io.i_p
  io.o_g := io.i_g
}

class PG extends Module {
  val io = IO(new Bundle {
    val i_a = Input(UInt(1.W))
    val i_b = Input(UInt(1.W))
    val o_p = Output(UInt(1.W))
    val o_g = Output(UInt(1.W))
  })
  io.o_p := io.i_a ^ io.i_b
  io.o_g := io.i_a & io.i_b
}