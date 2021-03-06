module Parser(module CoreParser, T, digit, digitVal, chars, letter, err,
              lit, number, iter, accept, require, token,
              spaces, word, (-#), (#-)) where
import Prelude hiding (return, fail)
import Data.Char
import CoreParser
infixl 7 -#, #-

type T a = Parser a

err :: String -> Parser a
err message cs = error (message++" near "++cs++"\n")

iter :: Parser a -> Parser [a]
iter m = m # iter m >-> cons ! return []

iter1 :: Parser a -> Int -> Parser [a]
iter1 m 0 = return []
iter1 m i = m # iter1 m (i-1) >-> cons

cons(a, b) = a:b

(-#) :: Parser a -> Parser b -> Parser b
m -# n = m # n >-> snd

(#-) :: Parser a -> Parser b -> Parser a
m #- n = m # n >-> fst

spaces :: Parser String
spaces =  iter cs
  where
    cs = char ? isSpace
-- iter (\cs -> (char ? isSpace) cs )

token :: Parser a -> Parser a
token m = m #- spaces

letter :: Parser Char
letter = char ? isAlpha
--  error "letter not implemented"

word :: Parser String
word = token (letter # iter letter >-> cons)

chars :: Int -> Parser String
chars = iter1 char

accept :: String -> Parser String
accept w = (token (chars (length w))) ? (==w)

require :: String -> Parser String
require w = (accept w ! err("Required string " ++ w ++ " is missing"))

lit :: Char -> Parser Char
lit c = token char ? (==c)

digit :: Parser Char
digit = char ? isDigit

digitVal :: Parser Integer
digitVal = digit >-> digitToInt >-> fromIntegral

number' :: Integer -> Parser Integer
number' n = digitVal #> (\ d -> number' (10*n+d))
          ! return n
number :: Parser Integer
number = token (digitVal #> number')
