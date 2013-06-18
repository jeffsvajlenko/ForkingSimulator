package main;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

import models.Operator;

public class OperatorChooser {
	
	private Operator[] operators;
	private int current;
	private LinkedList<Integer> otherOperators;
	private Random random;
	
	/**
	 * Initializes an OperatorChooser with the given operators, and the given current operator (index in array).
	 * @param operators An array of the operators to choose from.
	 * @param current The current operator.
	 */
	public OperatorChooser(Operator[] operators, int current) {
		Objects.requireNonNull(operators);
		if(current >= operators.length) {
			throw new IllegalArgumentException("Current is not a valid index in the array.");
		}
		
		this.current = current;
		this.operators = operators.clone();
		this.random = new Random();
		this.otherOperators = new LinkedList<Integer>();
		for(int i = 0; i < operators.length; i++) {
			if(i != current) {
				otherOperators.add(i);
			}
		}
	}
	
	/**
	 * Returns the current operator.
	 * @return the current operator.
	 */
	public Operator getCurrent() {
		return operators[current];
	}
	
	/**
	 * Returns a random operator, which is not the curreupt operator, without repeats.  Null if all operators have been randomly got.
	 * @return the operator, or null.
	 */
	public Operator getRandom() {
		if(otherOperators.size() == 0) {
			return null;
		} else {
			return operators[otherOperators.get(random.nextInt(otherOperators.size()))];
		}
	}
}
