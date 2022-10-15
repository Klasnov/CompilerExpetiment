package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.lexer.TokenKind;
import cn.edu.hitsz.compiler.parser.table.*;
import cn.edu.hitsz.compiler.symtab.SymbolTable;

import java.util.*;


/**
 * LR 语法分析驱动程序
 * 该程序接受词法单元串与 LR 分析表 (action 和 goto 表), 按表对词法单元流进行分析, 执行对应动作, 并在执行动作时通知各注册的观察者.
 * 你应当按照被挖空的方法的文档实现对应方法, 你可以随意为该类添加你需要的私有成员对象, 但不应该再为此类添加公有接口, 也不应该改动未被挖空的方法,
 * 除非你已经同助教充分沟通, 并能证明你的修改的合理性, 且令助教确定可能被改动的评测方法. 随意修改该类的其它部分有可能导致自动评测出错而被扣分.
 * @author HITSZ
 * @author Klasnov
 */
public class SyntaxAnalyzer {
    private final SymbolTable symbolTable;
    private final List<ActionObserver> observers = new ArrayList<>();
    private final LinkedList<Character> chrStk = new LinkedList<>();
    private final LinkedList<Status> sttStk = new LinkedList<>();
    private LinkedList<Token> tokens;
    private LRTable lrTable;

    public SyntaxAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    /**
     * 注册新的观察者
     * @param observer 观察者
     */
    public void registerObserver(ActionObserver observer) {
        observers.add(observer);
        observer.setSymbolTable(symbolTable);
    }

    /**
     * 在执行 shift 动作时通知各个观察者
     * @param currentStatus 当前状态
     * @param currentToken  当前词法单元
     */
    public void callWhenInShift(Status currentStatus, Token currentToken) {
        for (final var listener : observers) {
            listener.whenShift(currentStatus, currentToken);
        }
    }

    /**
     * 在执行 reduce 动作时通知各个观察者
     * @param currentStatus 当前状态
     * @param production    待规约的产生式
     */
    public void callWhenInReduce(Status currentStatus, Production production) {
        for (final var listener : observers) {
            listener.whenReduce(currentStatus, production);
        }
    }

    /**
     * 在执行 accept 动作时通知各个观察者
     * @param currentStatus 当前状态
     */
    public void callWhenInAccept(Status currentStatus) {
        for (final var listener : observers) {
            listener.whenAccept(currentStatus);
        }
    }

    /**
     * 初始化状态栈，记录词法分析后的字符串
     * @param tokens 词法分析后的字符串
     */
    public void loadTokens(LinkedList<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * 导入LR(1)分析表
     * @param table 已经完成的LR(1)分析表
     */
    public void loadLRTable(LRTable table) {
        this.lrTable = table;
    }

    /**
     * 运行语法分析程序
     */
    public void run() {
        boolean acc = false;
        Action action;
        // 初始化状态栈和字符栈
        chrStk.addFirst(new Character(Token.eof()));
        sttStk.addFirst(lrTable.getInit());
        // 读取输入字符串的内容
        while (!tokens.isEmpty()) {
            action = lrTable.getAction(sttStk.getFirst(), tokens.getFirst());
            // 根据LR(1)分析表内容进行移进规约
            switch (action.getKind()) {
                // 进行移进
                case Shift -> {
                    acc = false;
                    chrStk.addFirst(new Character(tokens.removeFirst()));
                    sttStk.addFirst(action.getStatus());
                    callWhenInShift(sttStk.getFirst(), chrStk.getFirst().getTkn());
                }
                // 进行规约
                case Reduce -> {
                    acc = false;
                    Production p = action.getProduction();
                    for (int i = 0; i < p.body().size(); i++) {
                        chrStk.removeFirst();
                        sttStk.removeFirst();
                    }
                    chrStk.addFirst(new Character(p.head()));
                    sttStk.addFirst(lrTable.getGoto(sttStk.getFirst(), p.head()));
                    callWhenInReduce(sttStk.getFirst(), p);
                }
                // 完成识别
                case Accept -> {
                    if (tokens.getFirst().getKind() == TokenKind.eof()) {
                        tokens.removeFirst();
                        acc = true;
                    }
                }
                // 非法句型
                default -> {
                    throw new RuntimeException("Syntax analysis failed! " +
                            "It is not a sentence pattern corresponding to grammar.");
                }
            }
        }
        if (acc) {
            callWhenInAccept(sttStk.getFirst());
        }
    }
}
