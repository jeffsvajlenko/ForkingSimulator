% Mutate by renaming of an identifier or type in any program in any C-like language
#pragma -char -comment

% C tokens, so we don't break them up
include "cstokens.grm"

% Separate sequences of spaces and tabs so they cn be individually added or deleted
tokens
	space	" "
|	"	"
end tokens

% Our grammar simply processes lines of text
define program
	[repeat line]
	[repeat lineitem] 	% Some files don't have a final newline
end define

define line
	[repeat lineitem] [newline]
end define

define lineitem
	[space_comment]
|	[space]
|	[literal]
|	[id_or_number]
|	[not newline] [token]
end define

define space_comment
	[repeat space+] [comment]
end define

define id_or_number
	[id]
|	[key]
end define

define literal
	[number]
|	[stringlit]
|	[charlit]
end define

% We need to mutate randomly, so use the new TXL random module (Linux/Unix only, no Windows)
include "random.mod"

% Main rule - initialize everything then mutate
function main
	% Probability of mutation is 1/N, that is, 1 in N instances will be mutated
	export N [number]
	1501
	export CheckN [number]
	0
	% We need an example of a space to insert 
	construct SpaceNewline [opt line]
		_ [parse " "]
	deconstruct SpaceNewline
		Space [space] Newline [newline]
	export Space 
	export Newline
	% Initialize the random number seed
	construct _ [id]
		_ [pragma "-token"] [randinit] [pragma "-char"]
	% Apply mutators
	replace [program]
		P [program]
	by
		P [checkMutatable] [mutate P]
end function

%[stringlit] [charlit] [number]

% Can we mutate this input?
function checkMutatable
	match [program]
		P [program]
	deconstruct not * [literal] P
		Id [literal]
	construct Message [number]
		_ [message "*** ERROR: mutate_literal cannot mutate this file"] [quit 99]
end function

% Mutate randomly until something changes
function mutate OriginalP [program]
	replace [program] 
		P [program]
	deconstruct P
		OriginalP
	by
		P [mutateNumber] [mutate OriginalP]
end function

% Choose some number and change it
rule mutateNumber
	import N [number]
	import CheckN [number]
	replace $ [number]
		Id [number]
	construct Random [number]
		_ [rand N]
	deconstruct not CheckN
		1
	export CheckN
		Random
	deconstruct Random
			1
   	construct _ [id]
			_ [pragma "-token"] [randint] [pragma "-char"]
	import RandInt [number]
	deconstruct not Id
		RandInt
	by
		RandInt
end rule

function randint
	construct _ [number]
	_ [system "bash -c 'echo $RANDOM' > _rand_"]
	construct RandInt [number]
	_ [read "_rand_"]
	construct _ [number]
		_ [system "/bin/rm -f _rand_"]
	export RandInt
	match [any]
		_ [any]
end function
