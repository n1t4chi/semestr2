%% coding: latin-1
%%przedszkole.erl
-module(przedszkole).
-export([przedszkolanka/1]).


loop(N,Dzieci) ->
    receive
        {init,IleDzieci} ->
            io:format("Przedszkolanka bedzie sie opiekowac max ~p dziecmi~n",[IleDzieci]),
            loop(IleDzieci,Dzieci);
        {Kto,pozostaw,ImieDziecka} -> 
            case N =:= length(Dzieci) of 
            true ->
                io:format("Nie mozna przyjac kolejnego dziecka!~n"
                    ++"Aktualny stan dzieci ~p/~p~n"
                    ++"Obecne:~p~n"
                    ,[length(Dzieci),N,Dzieci]),
                loop(N,Dzieci);
            false -> 
                %dzieci o tym samym imieniu:
                %case 1, zak³adamy ¿e rodzic nie moze mieæ 2 dzieci o tym samym imieniu:
                case lists:member({Kto,ImieDziecka},Dzieci) of 
                true -> 
                    io:format("Juz jest dziecko ~p wsrod ~p~n",[{Kto,ImieDziecka},Dzieci]), 
                    loop(N,Dzieci);
                false -> 
                    case rpc(Kto,{odebrano,ImieDziecka}) =:= {error} of
                    false ->
                        NoweDzieci = [{Kto,ImieDziecka}|Dzieci],
                        io:format("Przyjeto dziecko:~p~n"
                            ++"Aktualny stan dzieci ~p/~p~n"
                            ++"Obecne:~p~n"
                            ,[{Kto,ImieDziecka},length(NoweDzieci),N,NoweDzieci]),
                        loop(N,NoweDzieci);
                    true ->
                        io:format("Brak potwierdzenia przyjecia dziecka:~p~n"
                            ++"Aktualny stan dzieci ~p/~p~n"
                            ++"Obecne:~p~n"
                            ,[{Kto,ImieDziecka},length(Dzieci),N,Dzieci]),
                        loop(N,Dzieci)
                    end
                end
                %case 2, zak³adamy ¿e mo¿e:
                %NoweDzieci = [{Kto,ImieDziecka}|Dzieci],
                %io:format("Przyjeto dziecko:~p~nAktualny stan:~p~n",[{Kto,ImieDziecka},NoweDzieci]),
                %loop(N,NoweDzieci)
            end;
        {Kto,odbierz,ImieDziecka} -> 
            case lists:member({Kto,ImieDziecka},Dzieci) of
            true -> 
                case rpc(Kto,{wydano,ImieDziecka}) =:= {error} of
                false ->
                    NoweDzieci = lists:delete({Kto,ImieDziecka},Dzieci),
                    io:format("Odebrano dziecko,~p~n"
                        ++"Aktualny stan dzieci ~p/~p~n"
                        ++"Obecne:~p~n"
                        ,[{Kto,ImieDziecka},length(NoweDzieci),N,NoweDzieci]),
                    loop(N,NoweDzieci);
                true ->
                    io:format("Brak potwierdzenia odebrania dziecka:~p~n"
                        ++"Aktualny stan dzieci ~p/~p~n"
                        ++"Obecne:~p~n"
                        ,[{Kto,ImieDziecka},length(Dzieci),N,Dzieci]),
                    loop(N,Dzieci)
                end;
            false -> 
                io:format("Nie ma dziecka ~p wsrod ~p~n"
                    ,[{Kto,ImieDziecka},Dzieci]), 
                loop(N,Dzieci)
            end;
        Other ->  
            io:format("Unknown message~p~n",[Other]),
            loop(N,Dzieci)
    end.

%c(przedszkole).
%Pid = przedszkole:przedszkolanka(2).
%Pid ! {self(),pozostaw,adam}.
%Pid ! {self(),pozostaw,tomek},Pid ! ok.
%Pid ! {self(),odbierz,tomek}.
%Pid ! {self(),odbierz,adam},Pid ! ok.

%Pid ! ok.

rpc(Pid, Request) ->
    Pid ! {self(), Request},
    receive
    Response -> Response
    after 20000 -> {error}
    end.


task() -> loop(0,[]).

przedszkolanka(N) -> 
    Pid = spawn(fun task/0),
    Pid ! {init,N},
    Pid.