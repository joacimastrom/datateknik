-- right f s (x:xs) = f s x : scanr f (f s x) xs
-- right f s [] = [s]
--
-- scanrR f z []      = [z]
-- scanrR f z (x:xs)  = (:) (f x (head (scanrR f z xs))) (scanrR f z xs)


left f s [] = [s]
left f s (x:xs) = s: left f (f s x) xs
left' f = foldl (\s (xs) -> s ++ )

scanlF f z xs = foldl (\xs y -> xs ++ [(f (last xs) y)]) [z] xs

reverse :: [a] -> [a]
reverse [a] = foldl (flip (:)) []



-- scanlF f z l = foldl ff [z] l
--    where ff xs x = xs ++ (f (last xs) x):[]
--
--
-- scanrF f= foldr (\x ys ->f (head ys) x: ys)


-- scanr' f i (x:xs) = f (head scanr' f i xs) x: scanr' f i xs
-- scanr' f i [] = [i]
