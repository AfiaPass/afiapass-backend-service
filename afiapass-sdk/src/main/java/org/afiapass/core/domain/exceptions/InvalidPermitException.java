package org.afiapass.core.domain.exceptions;

import org.afiapass.general.exception.AfiaPassException;

public class InvalidPermitException extends AfiaPassException {
    public InvalidPermitException(String message) {
        super(message);
    }
}
