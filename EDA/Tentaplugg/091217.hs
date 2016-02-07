

map' f = foldl (\z x -> z ++ [f x]) []
z = []

filter' xs ys = [ x | x  <- xs, x ´elem´ ys]
