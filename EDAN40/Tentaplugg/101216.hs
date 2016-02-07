-- 1.
f x = 5 + 8/x
f = (5+) . (/) 8

f x y = 3*y + x
f = flip ((+) . (*3))

-- 2.
[f x | x <- [y + 4 | y <- ys, y<5]]
map (f . (4+)) (filter (<5))   -- facit har punkt mellan paranteser?

-- 3.
deriving Enum


-- 4.
{- Pure functional language means it has no side effects,
nothing is stored internally that will change the result of an execution

Predictable, but no states, or interaction with outside world-}

-- 5.
[[Char]] ["christmas", "christmas", "christmas", "christmas", "christmas"]

-- 6.
