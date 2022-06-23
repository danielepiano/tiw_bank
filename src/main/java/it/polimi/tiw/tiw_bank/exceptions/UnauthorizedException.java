package it.polimi.tiw.tiw_bank.exceptions;

public class UnauthorizedException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnauthorizedException() {
		super("Unauthorized for this operation.");
	}
	public UnauthorizedException(String message) {
		super(message);
	}

}
