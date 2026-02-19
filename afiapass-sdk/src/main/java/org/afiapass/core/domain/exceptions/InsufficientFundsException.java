package org.afiapass.core.domain.exceptions;

import org.afiapass.general.exception.AfiaPassException;

public class InsufficientFundsException extends AfiaPassException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
