-module(primer).
-export([start/0, slave/1, primer/1]).


is_prime_loop(N, K) ->
    K2 = K*K, R = N rem K,
    case (K2 =< N) and (R /= 0) of
        true -> is_prime_loop(N, K+1);
        false -> K
    end.

is_prime(N) ->
    K = is_prime_loop(N, 2),
    (N >= 2) and (K*K > N).

n2s(N) ->
    lists:flatten(io_lib:format("~p", [N])).

slave(Id) ->
    receive
        {isprime, N} ->
            case is_prime(N) of 
                true -> io:fwrite("(" ++ n2s(Id) ++ ") " ++ n2s(N) ++ "\n");
                false -> []
            end,
            slave(Id)
    end.

create_slaves(Max, Max) -> [];
create_slaves(Id, Max) ->
    Slave = spawn(primer, slave, [Id]),
    [Slave|create_slaves(Id+1, Max)].

primer(Slaves) ->
    receive
        {init, N} when N =< 0 ->
            throw({nonpositive, N});
        {init, N} ->
            primer(create_slaves(0, N));
        {isprime, _} when Slaves == [] ->
            throw({uninitialized});
        {isprime, N} when N=<0 ->
            throw({nonpositive, N});
        {isprime, N} ->
            SlaveId = N rem length(Slaves),
            lists:nth(SlaveId + 1, Slaves) ! {isprime, N},
            primer(Slaves)
    end.


spam(_, N, N) -> true;
spam(Primer, N, Max) -> 
    Primer ! {isprime, N},
    spam(Primer, N+1, Max).

start() ->
    Primer = spawn(primer, primer, [[]]),
    Primer ! {init, 7},
    spam(Primer, 2, 100).