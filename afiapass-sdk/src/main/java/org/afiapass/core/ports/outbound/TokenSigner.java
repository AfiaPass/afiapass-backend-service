package org.afiapass.core.ports.outbound;

import org.afiapass.core.domain.data.models.Permit;

public interface TokenSigner {
    String generateOfflineToken(Permit permit);
}
