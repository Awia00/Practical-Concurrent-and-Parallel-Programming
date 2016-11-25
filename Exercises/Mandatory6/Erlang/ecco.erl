-module(ecco).
-export([start/0,person/0,ecco/0]).

substr(S) when length(S) < 6 -> "..." ++ S; 
substr([_|T]) -> substr(T). 

person() ->
    receive
        {start, Pid} ->
            S = "Hvad drikker Moller",
            io:fwrite("[says]: " ++ S ++ "\n"),
            Pid ! {self(), {message, S}} ;
        {message, S} -> 
            io:fwrite("[hears]: " ++ S ++ "\n")
    end,
    person().

ecco() ->
    receive
        {Sender, {message, S}} ->
            Sub = substr(S),
            Sender ! {message, Sub},
            Sender ! {message, Sub},
            Sender ! {message, Sub},
            ecco()
    end.

start() -> 
    Person = spawn(ecco, person, []),
    Ecco = spawn(ecco, ecco, []),
    Person ! {start, Ecco}.
