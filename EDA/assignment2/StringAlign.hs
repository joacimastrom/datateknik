scoreMatch = 0
scoreMismatch = -1
scoreSpace = -1
a = "writers"
b = "vintner"

similarityScore :: [Char] -> [Char] -> Int
similarityScore [] ys = scoreSpace * length ys
similarityScore (xs) [] = scoreSpace * length xs
similarityScore (x:xs) (y:ys) = maximum [similarityScore xs ys + score x y, similarityScore xs (y:ys) + score x '-', similarityScore (x:xs) ys + score '-' y]

similarityScore2 :: [Char] -> [Char] -> Int
similarityScore2 xs ys = sem2 (length xs) (length ys)
  where
    sem2 i j = semTable!!i!!j
    semTable = [[ semEntry i j | j<-[0..]] | i<-[0..] ]
    semEntry :: Int -> Int -> Int
    semEntry x 0 = x * scoreSpace
    semEntry 0 y = y * scoreSpace
    semEntry i j = score x y + maximum [sem2 (i-1) (j-1), sem2 i (j-1), sem2 (i-1) j]
        where
          x = xs!!(i-1)
          y = ys!!(j-1)

optAlignments2 :: String -> String -> [AlignmentType]
optAlignments2 xs ys = map f (snd (opt (length xs) (length ys)))
  where
      f (a, b) = (reverse a, reverse b)
      opt :: Int -> Int -> (Int, [AlignmentType])
      opt i j = optTable!!i!!j
      optTable = [[ optEntry i j | j<-[0..]] | i<-[0..] ]
      optEntry :: Int -> Int -> (Int, [AlignmentType])
      optEntry i 0 = (scoreSpace * i, [(replicate i '-', take i xs)])
      optEntry 0 j = (scoreSpace * j, [(replicate j '-', take j ys)])
      optEntry i j = (fst (head z), concatMap snd z)
          where
            a = opt (i - 1) (j - 1)
            b = opt (i - 1) j
            c = opt i (j - 1)
            x = xs !! (i - 1)
            y = ys !! (j - 1)
            z = maximaBy fst $ [ (fst a + score x y, attachHeads x y (snd a)),
                                 (fst b + score x '-', attachHeads x '-' (snd b)),
                                 (fst c + score '-' y, attachHeads '-' y (snd c)) ]

score :: Char -> Char -> Int
score x '-' = scoreSpace
score '-' y = scoreSpace
score x y
  | x == y = scoreMatch
  | x /= y = scoreMismatch

maximaBy :: Ord b => (a -> b) -> [a] -> [a]
maximaBy _ [] = []
maximaBy f xs = [ x | x <- xs, f x == maximum (map f xs)]

attachHeads :: a -> a -> [([a],[a])] -> [([a],[a])]
attachHeads h1 h2 aList = [(h1:xs,h2:ys) | (xs,ys) <- aList]

type AlignmentType = (String,String)

optAlignments :: String -> String -> [AlignmentType]
optAlignments (x:xs) [] = attachHeads x '-' (optAlignments xs [])
optAlignments [] (y:ys) = attachHeads '-' y (optAlignments [] ys)
optAlignments [] [] = [([],[])]
optAlignments (x:xs) (y:ys) = maximaBy matchScore (attachHeads x y (optAlignments xs ys) ++
  attachHeads x '-' (optAlignments xs (y:ys)) ++
    attachHeads '-' y (optAlignments (x:xs) ys)) --
      where
        matchScore :: ([Char], [Char]) -> Int
        matchScore (xs, []) = scoreSpace * length xs
        matchScore ([], ys) = scoreSpace * length ys
        matchScore ((x:xs), (y:ys)) = score x y + matchScore (xs, ys)

outputOptAlignments :: String -> String -> IO ()
outputOptAlignments x y = do
  let c = (optAlignments2 x y)
  putStrLn("There are " ++ (show (length c)) ++ " optimal alignments")
  mapM_ putStrLn (map mergeTuple c)
  where
    mergeTuple :: (String, String) -> String
    mergeTuple (a, b) = "\n" ++ a ++ "\n" ++ b
