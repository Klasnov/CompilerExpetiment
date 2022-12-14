package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.parser.table.Term;
import cn.edu.hitsz.compiler.utils.FilePathConfig;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 词法单元类型, 你不应该修改此文件
 * 这个类代表词法单元的类型. 由于根据实验设计, 词法单元的可能的类型需要从码点文件 (codingMap.txt) 中读取,
 * 为了既获得在运行期读取文件的灵活性, 又保证词法单元构造时一些关于类型的 typo 可以被检测到, 我们采取了一定的检查.
 * 同时, 由于后续实验过程还需要读取语法文件 (grammar.txt) 或 LR 分析表 (LR1_table.csv), 为了识别这些文件中的终止符与非终止符,
 * TokenKind 类还作为终结符, 供表示语法及产生式的类使用. 这就是为什么它继承了 Term (语法项) 作为父类.
 * 如果你目前在完成实验, 那么只需要关注用于构造 TokenKind 的 fromString 与 eof, 用于获得信息的 getIdentifier 与 getCode 即可.
 * 倘若你有空探寻一下代码的细致实现, 那么请详见代码中的实现注释, 并同时参考 SeeAlso 中的内容.
 *
 * @author HITSZ
 * @see Term 语法项 - 构成产生式的基础元素
 * @see cn.edu.hitsz.compiler.parser.table.NonTerminal 非终止符 - 可以通过产生式被其它项规约出来的项
 * @see cn.edu.hitsz.compiler.parser.table.Production 产生式 - BNF 语法描述的基本要素
 */

public class TokenKind extends Term {
    private final int code;

    private TokenKind(String id, int code) {
        super(id);
        this.code = code;
    }

    /** 允许用作 TokenKind 的 id 的字符串集合 */
    private static final Map<String, TokenKind> ALLOWED = new HashMap<>();
    private static final TokenKind EOF = new TokenKind("$", -1);

    /**
     * 从码点文件中读取允许的标识符集合
     */
    public static void loadTokenKinds() {
        if (!ALLOWED.isEmpty()) {
            throw new RuntimeException("Can not set allowed twice");
        }
        final var lines = FileUtils.readLines(FilePathConfig.CODING_MAP_PATH);
        for (final var line : lines) {
            final var words = line.split(" ");
            final var code = Integer.parseInt(words[0]);
            final var id = words[1];
            ALLOWED.put(id, new TokenKind(id, code));
        }
        ALLOWED.put("$", EOF);
    }

    /**
     * @param id 标识符
     * @return 该标识符是否被允许作为 TokenKind 的标识符
     */
    public static boolean isAllowed(String id) {
        if (ALLOWED == null) {
            throw new RuntimeException("Empty allowed");
        }
        return ALLOWED.containsKey(id);
    }

    /**
     * @return 一个标识符到 TokenKind 的 Map, 其键集包含了所有允许的标识符
     */
    public static Map<String, TokenKind> allAllowedTokenKinds() {
        return Collections.unmodifiableMap(ALLOWED);
    }

    /**
     * @param id 标识符
     * @return 从给定的标识符中构造出的 TokenKind
     * @throws RuntimeException 码点文件尚未被读取, 或该标识符不被允许作为 TokenKind 的标识符
     */
    public static TokenKind fromString(String id) {
        if (ALLOWED == null || !ALLOWED.containsKey(id)) {
            throw new RuntimeException("Illegal Identifier");
        }
        return ALLOWED.get(id);
    }

    /**
     * @return 代表 EOF 的 TokenKind
     */
    public static TokenKind eof() {
        return EOF;
    }

    /**
     * @return 获得该 TokenKind 的标识符
     */
    public String getIdentifier() {
        return getTermName();
    }

    /**
     * @return 获得该 TokenKind 的码点
     */
    public int getCode() {
        return code;
    }
}
