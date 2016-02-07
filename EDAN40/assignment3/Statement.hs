module Statement(T, parse, toString, fromString, exec) where
import Prelude hiding (return, fail)
import Parser hiding (T)
import qualified Dictionary
import qualified Expr
type T = Statement
data Statement =
    Assignment String Expr.T |
    Skip |
    Begin [Statement] |
    If Expr.T Statement Statement |
    While Expr.T Statement |
    Read String |
    Write Expr.T
    deriving Show

statement = assignment ! skip ! begin ! ifStm ! while ! readStm ! write

assignment = word #- accept ":=" # Expr.parse #- require ";" >-> buildAss
buildAss (v, e) = Assignment v e

skip = accept "skip" #- require ";" >-> buildSkip
buildSkip _ = Skip

begin = accept "begin" -# iter statement #- require "end" >-> Begin

ifStm = accept "if" -# Expr.parse #- require "then" # statement #- require "else" # statement >-> buildIf
buildIf ((x, y), z) = If x y z

readStm = accept "read" -# word #- require ";" >-> Read

write = accept "write" -# Expr.parse #- require ";" >-> Write

while = accept "while" -# Expr.parse #- require "do" # statement >-> buildWhile
buildWhile (x, y) = While x y


exec :: [T] -> Dictionary.T String Integer -> [Integer] -> [Integer]
exec [] _ _ = []
exec (If cond thenStmts elseStmts: stmts) dict input =
    if (Expr.value cond dict)>0
    then exec (thenStmts: stmts) dict input
    else exec (elseStmts: stmts) dict input

exec (Skip:stmts) dict input = exec stmts dict input

exec (Assignment v expr:stmts) dict input = exec stmts (Dictionary.insert (v, Expr.value expr dict) dict ) input

exec (While e s:stmts) dict i
    | Expr.value e dict > 0 = exec (s : While e s : stmts) dict i
    | otherwise             = exec stmts dict i

exec (Read v: stmts) dict (current:rest) =  exec stmts (Dictionary.insert (v, current) dict ) rest

exec (Write expr: stmts) dict input = [Expr.value expr dict] ++ (exec stmts dict input)

exec (Begin stmt:stmts) dict input = (exec (stmt++stmts) dict input)

indent = flip take (repeat '\t')

buildString :: Int -> Statement -> String
buildString n (Assignment v e)   = indent n ++ v ++ " := " ++ Expr.toString e ++ "\n"
buildString n (Skip)             = indent n ++ "skip;\n"
buildString n (Begin stmts)      = indent n ++ "begin\n" ++ concat (map (buildString (n+1)) stmts) ++ indent n ++ "end\n"
buildString n (If cond s1 s2)    = indent n ++ "if " ++ Expr.toString cond ++ " then\n" ++ buildString (n+1) s1 ++ indent n ++ "else\n" ++ buildString (n+1) s2
buildString n (While cond stmts) = indent n ++ "while " ++ Expr.toString cond ++ " do\n" ++ buildString (n+1) stmts
buildString n (Read s)           = indent n ++ "read " ++ s ++ ";\n"
buildString n (Write s)          = indent n ++ "write " ++ Expr.toString s ++ ";\n"

instance Parse Statement where
  parse = statement
  toString = buildString 0
