# Mandatory 6
*Anders Wind - awis@itu.dk*

## a)
Solved, see src/ABC/*

## b)
By using this approach we introduce the shared state again destroying the atomicity of the current approach. We exactly get the problem that is otherwise dimished by using message parsing. 
In particular any given Bank who changes an account is now also responsible for handeling amount, which means that two banks can read a value and set it to some value, overwriting the other´s changes.