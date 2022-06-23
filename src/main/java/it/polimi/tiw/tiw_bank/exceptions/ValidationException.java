package it.polimi.tiw.tiw_bank.exceptions;

public class ValidationException extends Exception {

	private static final long serialVersionUID = 1L;

	public ValidationException() {
		super("Validation exception occured.");
	}
	public ValidationException(String message) {
		super(message);
	}

}
