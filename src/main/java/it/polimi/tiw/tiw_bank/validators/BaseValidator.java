package it.polimi.tiw.tiw_bank.utils;

import it.polimi.tiw.tiw_bank.exceptions.ValidationException;

import java.util.regex.Pattern;

public class Validator {
	public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
	public static final String ALPHA_PATTERN = "^[ A-Za-z]+$";

	/**
	 * Check conformità email a standard RFC 5322.
	 * @param email
	 * @return
	 * @throws ValidationException
	 */
	public static boolean rule_validEmail(String email) throws ValidationException {
		if ( Pattern.compile(EMAIL_PATTERN).matcher(email).matches() ) {
			return true;
		}
		throw new ValidationException("Invalid email.");
	}

	/**
	 * Check formato password: minimo 8 caratteri e almeno una lettera maiuscola, una lettera minuscola, un numero,
	 * un carattere speciale.
	 * @param password
	 * @return
	 * @throws ValidationException
	 */
	public static boolean rule_validPassword(String password) throws ValidationException {
		if ( Pattern.compile(PASSWORD_PATTERN).matcher(password).matches() ) {
			return true;
		}
		throw new ValidationException("Invalid password: use minimum eight characters and at least one uppercase letter," +
				" one lowercase letter, one number and one special character.");
	}

	/**
	 * Check match tra password e conferma.
	 * @param password
	 * @param confirmPassword
	 * @return
	 * @throws ValidationException
	 */
	public static boolean rule_matchingPassword(String password, String confirmPassword) throws ValidationException {
		if ( password.equals(confirmPassword) ) {
			return true;
		}
		throw new ValidationException("Password fields don't match.");
	}

	/**
	 * Check campo obbligatorio compilato.
	 * @param varName
	 * @param var
	 * @return
	 * @throws ValidationException
	 */
	public static boolean rule_required(String varName, String var) throws ValidationException {
		if ( var != null && !var.isBlank() ) {
			return true;
		}
		throw new ValidationException( capitalize(varName) + " required." );
	}

	/**
	 * Check stringa solo lettere e spazi.
	 * @return
	 * @throws ValidationException
	 */
	public static boolean rule_alpha(String varName, String string) throws ValidationException {
		if ( Pattern.compile(ALPHA_PATTERN).matcher(string).matches() ) {
			return true;
		}
		throw new ValidationException("Invalid " + varName + ": only letters and spaces allowed.");
	}



	private static String capitalize(String str) {
		if ( str == null ) {
			return str;
		} else {
			str = str.trim();
			if ( str.length() >= 1 ) {
				return str.trim().substring(0, 1).toUpperCase() + str.substring(1);
			}
			return str;
		}
	}
}
