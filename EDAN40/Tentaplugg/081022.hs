-- map (:)   [(a, [a])] -> [[a]]   // [a] -> [[a] -> [a]]

-- [[Char]]  ["world", "world", "world", "world", "world"]

-- pure functional. Only what is coded is executed, nothing is stored. Same input to a function always yields same result.

-- curry


delete x [] = []
delete x (y:ys) = if x == y then ys else y : delete x ys

-- sätter ihop en lista av listor av alla


filter' p = foldr ( -> if p x then x:xs else xs) []

filter' p = foldr (\s xs -> if p s then s : xs else xs  ) [] 
