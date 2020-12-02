package ppa

import basic._
import chisel3._
import chisel3.{Bundle, Input, Module, Output, UInt, Vec}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object ReadCT {
 
  def readLine(filePath:String) = {
    val source = Source.fromFile(filePath, "UTF-8")
    //或取文件中所有行
    //val lineIterator = source.getLines()
    //迭代打印所有行
    //lineIterator.foreach()
    //将所有行放到数组中
    val lines = source.getLines().toArray
    source.close()
    println(lines.size)
    lines
  }
 
  def isEmpty(s: String): Boolean = (s == null) || (s.size==0)
}

class Compressor extends Module {
  val array = ReadCT.readLine("/home/jxzhang/projects/multiplier-generator/matrix/8bit_PPA.out")
  
  val inout = array(0).trim.split(" ")

  val inputWidth = inout(0).toInt
  println(inputWidth)

  val outputWidth = inout(1).toInt
  println(outputWidth)

  val io = IO(new Bundle {
//    val multiplicand = Input(UInt(m.W))
//    val multiplier = Input(UInt(n.W))
    val in = Output(Vec(inputWidth, UInt(1.W)))
    val out = Output(Vec(outputWidth, UInt(1.W)))
  })
  var wireMap = Map[Int, Data]()
  

  for (i <- 0 until inputWidth) {
    wireMap += i -> io.in(i)
  }

  val num = array(1).trim.toInt 
  for (i <- 2 until (num+2)) {
    val loc = array(i).trim.split(" ")
    if (loc.size == 5) {
      val a = Module(new FullAdder)
      a.io.A := wireMap(loc(0).toInt)
      a.io.B := wireMap(loc(1).toInt)
      a.io.Cin := wireMap(loc(2).toInt)
      wireMap += loc(3).toInt -> a.io.Sum
      wireMap += loc(4).toInt -> a.io.Cout
    }
    if (loc.size == 4) {
      val b = Module(new HalfAdder)
      b.io.A := wireMap(loc(0).toInt)
      b.io.B := wireMap(loc(1).toInt)
      wireMap += loc(2).toInt -> b.io.Sum
      wireMap += loc(3).toInt -> b.io.Cout
    }
  }

  val outLineNum = 2 + num
  val outLine = array(outLineNum).trim.split(" ")
  val outSize = outLine.size

  val pp = Array.ofDim(outSize,2)
  pp(0) = array(outLineNum)
  pp(1) = array(outLineNum+1)
  val outArray = ArrayBuffer[Int]()
  for(x <- 0 until outSize){
    for (y <- 0 until 2){
      val a = pp(x)(y)
      if(a !=0){
        outArray.append(a)
      }
    }
  }

  for(i <- 0 until outputWidth){
    io.out(i) := wireMap(outArray(i))
  }
}

object test{
  def main(args: Array[String]): Unit = {
    val topDesign = () => new Compressor()
    chisel3.Driver.execute(Array("-td", "./RTL/ct"), topDesign)
    /*
    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), topDesign) {
      c => new PPATester(c)
    }

    iotesters.Driver.execute(Array("-tgvo", "on", "-tbn", "verilator"), () => new PPAAnd(5, 4)) {
      c => new PPATester(c)
    }*/
  }
}
/*
class PPATester(c: PPA) extends PeekPokeTester(c) {
  poke(c.io.multiplicand, 10)
  poke(c.io.multiplier, 4)
  
  step(1)
  println("The result of ppa multiplier is: " + peek(c.io.multiplier).toString())
  println("The result of ppa outs(0) is: " + peek(c.io.outs(0)).toString())
  println("The result of ppa outs(1) is: " + peek(c.io.outs(1)).toString())
}*/