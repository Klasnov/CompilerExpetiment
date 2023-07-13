# Simple Compiler

This repository contains a simple compiler project written in Java. The compiler is capable of performing syntax analysis, semantic analysis (using the LR(1) method), and other processes typically found in a basic compiler. It can compile a language that is similar to C but much simpler.

## Features

- Variable definition
- Assignment operation
- Addition operation
- Subtraction operation
- Multiplication operation
- Division operation
- Return operation

## Usage

1. Place your code in the file `./data/in/input_code.txt`, which contains the example code.
2. Remove the appropriate comment symbols in `Main.java` located at `./src/cn/edu/hitsz/compiler`.
3. Run `Main.java`.
4. The compiled output will be generated as `./data/out/assembly_language.asm` in the form of RISC-V assembly.
