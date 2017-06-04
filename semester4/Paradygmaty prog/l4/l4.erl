%% coding: latin-1
%%l4.erl
-module(l4).
-export([pythag/1]).
-export([left_rotation/1]).
nwd(A,B) when (B==0) -> A;
nwd(A,B) -> nwd(B,A rem B).
floor(X) when X < 0 ->
    T = trunc(X),
    case X - T == 0 of
        true -> T;
        false -> T - 1
    end;
floor(X) -> 
    trunc(X).
ceil(X) when X < 0 ->
    trunc(X);
ceil(X) ->
    T = trunc(X),
    case X - T == 0 of
        true -> T;
        false -> T + 1
    end.

%Zadanie 1:
%a=m^2-n^2
%b=2mn
%c=m^2+n^2
%trojki: {ka,kb,kc} gdzie k- naturalne >0
%ograniczenie na m : 2m^2 <= kD <= 4m^2
%n moze siê zmieniac od 1 do m-1
%najmniejsza trojka ma sume 12 zatem K <= D/12

pythag(D) -> [{K*(M*M-N*N),K*(2*M*N),K*(M*M+N*N)} || 
        K <- lists:seq(1,ceil(D/12)), 
        M <- lists:seq(floor(math:sqrt(D/K)/2),ceil( math:sqrt(D/K/2))),
        N <- lists:seq(1,M-1),
        nwd(M,N) =:= 1,
        M*M-N*N < 2*M*N,
        2*K*M*(N+M)=:=D
    ].   

%Zadanie 2:
%l4:left_rotation({node, k1, v1, nil, nil}).
%l4:left_rotation({node, k1, v1, a , {node, k2, v2, b, c}}).
%wynik: {node,k2,v2,{node,k1,v1,a,b},c}
%l4:left_rotation({node, k1, v1, nil, {node, k2, v2, nil, nil}}).

left_rotation(N) ->
    case N of
        {node, Key, Value, Left, Right} -> 
            case Right of 
                {node, KeyR, ValueR, LeftR, RightR} ->
                    {node, KeyR, ValueR, {node, Key, Value, Left, LeftR}, RightR};
                _Else -> error
            end;
        _Else -> error
    end
.

%Zadanie 3: 