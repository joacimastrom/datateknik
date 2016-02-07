-- 1.
f = filter . flip elem
filter . flip elem :: (Eq a, Foldable t) => t a -> [a] -> [a]
-- kollar vilka värden som finns i båda listor t a och [a]

[x: | x <- xs, x `elem` ys]
