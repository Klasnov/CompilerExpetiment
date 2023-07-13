package cn.edu.hitsz.compiler;

import cn.edu.hitsz.compiler.asm.AssemblyGenerator;
import cn.edu.hitsz.compiler.lexer.LexicalAnalyzer;
import cn.edu.hitsz.compiler.lexer.TokenKind;
import cn.edu.hitsz.compiler.parser.IRGenerator;
import cn.edu.hitsz.compiler.parser.ProductionCollector;
import cn.edu.hitsz.compiler.parser.SemanticAnalyzer;
import cn.edu.hitsz.compiler.parser.SyntaxAnalyzer;
import cn.edu.hitsz.compiler.parser.table.GrammarInfo;
import cn.edu.hitsz.compiler.parser.table.TableGenerator;
import cn.edu.hitsz.compiler.parser.table.TableLoader;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FilePathConfig;
import cn.edu.hitsz.compiler.utils.FileUtils;
import cn.edu.hitsz.compiler.utils.IREmulator;

import java.io.IOException;
import java.util.Objects;

/**
 * @author HITSZ, Klasnov
 */

public class Main {
    public static void main(String[] args) throws IOException {
        // Build symbol tables for use by various parts
        TokenKind.loadTokenKinds();
        final var symbolTable = new SymbolTable();

        // lexical analysis
        final var lexer = new LexicalAnalyzer(symbolTable);
        lexer.loadFile(FilePathConfig.SRC_CODE_PATH);
        lexer.run();
        lexer.dumpTokens(FilePathConfig.TOKEN_PATH);
        final var tokens = lexer.getTokens();
        symbolTable.anlTkn(tokens);
        symbolTable.dumpTable(FilePathConfig.OLD_SYMBOL_TABLE);

        // Construct LR analysis table from grammar.txt
        final var tableGenerator = new TableGenerator();
        tableGenerator.run();
        final var lrTable = tableGenerator.getTable();
        lrTable.dumpTable("data/out/lrTable.csv");

        // Load LR analysis driver
        final var parser = new SyntaxAnalyzer(symbolTable);
        parser.loadTokens(tokens);
        parser.loadLRTable(lrTable);

        // Join the Observer that generates the list of specifications
        final var productionCollector = new ProductionCollector(GrammarInfo.getBeginProduction());
        parser.registerObserver(productionCollector);

        // Add Observer for semantic checking
        final var semanticAnalyzer = new SemanticAnalyzer();
        parser.registerObserver(semanticAnalyzer);

        // Add Observer for IR generation
        final var irGenerator = new IRGenerator();
        parser.registerObserver(irGenerator);

        // Perform syntax parsing and call each Observer in turn during parsing
        parser.run();

        // Output results of each Observer
        productionCollector.dumpToFile(FilePathConfig.PARSER_PATH);
        symbolTable.dumpTable(FilePathConfig.NEW_SYMBOL_TABLE);
        final var instructions = irGenerator.getIR();
        irGenerator.dumpIR(FilePathConfig.INTERMEDIATE_CODE_PATH);

        // Simulate the execution of IR and output the result
        // final var emulator = IREmulator.load(instructions);
        // FileUtils.writeFile(FilePathConfig.EMULATE_RESULT, emulator.execute().map(Objects::toString).orElse("No return value"));

        // Generate assembly file from IR
        final var asmGenerator = new AssemblyGenerator();
        asmGenerator.loadIR(instructions);
        asmGenerator.run();
        asmGenerator.dump(FilePathConfig.ASSEMBLY_LANGUAGE_PATH);
    }
}
