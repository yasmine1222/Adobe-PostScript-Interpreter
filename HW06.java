import java.util.*;


class HW06 {
    static class Token {
        int type;
        private boolean           _valueIfTypeBoolean;
        private double            _valueIfTypeDouble;
        private String            _valueIfTypeString;
        private ArrayList<Token>  _valueIfTypeList;
        
        static final int TYPE_BOOLEAN = 0;
        static final int TYPE_DOUBLE  = 1;
        static final int TYPE_STRING  = 2;
        static final int TYPE_LIST    = 3;
        static final String[] _typeNames = { "boolean", "double", "String", "list" };
        static String stringFromType(int type) { return _typeNames[type]; }
        
        Token(boolean          value) { type = TYPE_BOOLEAN; _valueIfTypeBoolean = value; } 
        Token(double           value) { type = TYPE_DOUBLE;  _valueIfTypeDouble  = value; }
        Token(String           value) { type = TYPE_STRING;  _valueIfTypeString  = value; }
        Token(ArrayList<Token> value) { type = TYPE_LIST;    _valueIfTypeList    = value; }
        
        public String toString() {
            if (type == TYPE_BOOLEAN) { return "" + _valueIfTypeBoolean; }
            if (type == TYPE_DOUBLE ) { return "" + _valueIfTypeDouble;  }
            if (type == TYPE_STRING ) { return "" + _valueIfTypeString;  }
            if (type == TYPE_LIST   ) { return "" + _valueIfTypeList;  }
            assert false : "Token type not recognized; Token type = " + type;
            return "";
        }
        
        boolean           getBoolean() { assert type == TYPE_BOOLEAN : "Token type is " + stringFromType(type); return _valueIfTypeBoolean; }
        double            getDouble()  { assert type == TYPE_DOUBLE  : "Token type is " + stringFromType(type); return _valueIfTypeDouble;  }
        String            getString()  { assert type == TYPE_STRING  : "Token type is " + stringFromType(type); return _valueIfTypeString;  }
        ArrayList<Token>  getList()    { assert type == TYPE_LIST    : "Token type is " + stringFromType(type); return _valueIfTypeList;    }
        
        static ArrayList<Token> tokenize(String postScriptProgram) {
            ArrayList<Token> result = new ArrayList<>();
            ArrayList<Token> list = null;
            ArrayList<Token> target = result;
            Scanner scanner = new Scanner(postScriptProgram);
            while (scanner.hasNext()) {
                if (scanner.hasNextDouble()) {
                    target.add(new Token(scanner.nextDouble()));
                } else if (scanner.hasNextBoolean()) {
                    target.add(new Token(scanner.nextBoolean()));
                } else {
                    String string = scanner.next().trim();
                    if (string.equals("{")) {
                        target = list = new ArrayList<Token>();
                    } else if (string.equals("}")) {
                        target = result;
                        target.add(new Token(list));
                    } else if (string.length() != 0) {
                        target.add(new Token(string));
                    }
                }
            }
            return result;
        }
    }
    
    static class Interpreter {
        Stack<Token> _stack; 
        HashMap<String, Token> _map;
        
        Interpreter() {
            _stack = new Stack<>();
            _map = new HashMap<>();
        }
        
        
        Token             stackPopToken()   { assert _stack.size() > 0 : "can't pop; stack is empty"; return _stack.pop(); }
        boolean           stackPopBoolean() { Token token = stackPopToken(); return token.getBoolean(); }
        double            stackPopDouble()  { Token token = stackPopToken(); return token.getDouble();  }
        String            stackPopString()  { Token token = stackPopToken(); return token.getString();  }
        ArrayList<Token>  stackPopList()    { Token token = stackPopToken(); return token.getList();  }
        
        void stackPush(Token            token) { _stack.push(token);            }
        void stackPush(boolean          value) { _stack.push(new Token(value)); }
        void stackPush(double           value) { _stack.push(new Token(value)); }
        void stackPush(String           value) { _stack.push(new Token(value)); }
        void stackPush(ArrayList<Token> value) { _stack.push(new Token(value)); }
        
        void stackPrint() {
            ArrayList<Token> tmp = new ArrayList<>();
            for (Token token : _stack) { tmp.add(token); }
            for (int i = tmp.size() - 1; i >= 0; --i) { System.out.println(tmp.get(i)); }
        }
        
        
        boolean mapContainsKey(String key) { return _map.containsKey(key); }
        void mapPut(String key, Token value) {
            _map.put(key, value);
        }
        Token mapGet(String key) { assert mapContainsKey(key); return _map.get(key); }
        void mapPrint() { System.out.println(_map); }
        
        
        void interpret(ArrayList<Token> tokens) {
            boolean PRINT_TOKEN = true;
            boolean PRINT_STACK_AND_MAP = true; // NOTE: only prints map if nonempty
            boolean PRINT_BEGIN_AND_END_INTERPRETING = true;
            if (PRINT_BEGIN_AND_END_INTERPRETING) { System.out.println("\n- BEGIN INTERPRETING " + tokens + " -\n\n"); }
            for (Token token : tokens) {
                if (PRINT_TOKEN) { System.out.println("> " + token + "\n"); }
                interpret(token);
                if (PRINT_STACK_AND_MAP) { stackPrint(); System.out.println("----- \nstack"); if (_map.size() > 0) { System.out.print(" + map "); mapPrint(); } System.out.println("\n"); }
            }
            if (PRINT_BEGIN_AND_END_INTERPRETING) { System.out.println("-- END INTERPRETING " + tokens + " --\n\n"); }
        }
        
        void interpret(Token token) {
            if (token.type == Token.TYPE_DOUBLE){
                stackPush(token);
            }
            if (token.type == Token.TYPE_BOOLEAN){
                stackPush(token);
            }
            if (token.type == Token.TYPE_LIST){
                stackPush(token);
                
            }
            if (token.type == Token.TYPE_STRING){
                String string = token.getString();
                
                if (string.charAt(0) == '/') { 
                    stackPush(string.substring(1));
                } 
                else if (string.equals("sub")){
                    double val1 = stackPopDouble();
                    double val2 = stackPopDouble();
                    double result = val1-val2;
                    stackPush(result);
                }
                else if (string.equals("add")){
                    double val1 = stackPopDouble();
                    double val2 = stackPopDouble();                 
                    double result = val1 + val2;
                    stackPush(result);
                    
                }
                
                else if (string.equals("mul")){
                    double val1 = stackPopDouble();
                    double val2 = stackPopDouble();
                    double result = val1*val2;
                    stackPush(result);
                }
                else if (string.equals("dup")){
                    double val1 = stackPopDouble();                  
                    stackPush(val1);
                    stackPush(val1);
                }
                else if (string.equals("exch")){
                    double val1 = stackPopDouble();
                    double val2 = stackPopDouble();
                    double result = Math.sqrt(val1*val1);
                    stackPush(val1);
                    stackPush(val2);
                }
                
                else if (string.equals("def")){
                    Token val = stackPopToken();
                    String key = stackPopString();
                    mapPut(key, val);
                    
                }
                else if (string.equals("sqrt")){
                    double val1 = stackPopDouble();                    
                    double result = Math.sqrt(val1);
                    stackPush(result);                                       
                }
                else if (string.equals("eq")){
                    double val1 = stackPopDouble();
                    double val2 = stackPopDouble();                 
                    stackPush(val1 == val2);
                }
                else if (mapContainsKey(string)){
                    Token value = mapGet(string);
//                    value.getList;
                    if (value.type == Token.TYPE_LIST){
                        interpret(value.getList());                  
                    }
                    else {
                        interpret(value);
                        
                    }
                    
                    
                    
                    
                }
            } 
        }
        
    }
        public static void main(String[] arguments) {
            
            //String program = "5.0 2.0 sub 3.0 eq";
            //String program = "/pi 3.14 def";
            //String program = "/pi 3.14 def /tau pi pi add def";
            String program = "/pythag { dup mul exch dup mul add sqrt } def 3.0 4.0 pythag";
            
            ArrayList<Token> tokens = Token.tokenize(program);
            
            Interpreter interpreter = new Interpreter();
            interpreter.interpret(tokens);
        }
    }
