package whist;

import java.io.Serializable;

public enum Command implements Serializable {
    // SERVER
    PLAY,
    // CLIENT
    CARD_RESPONSE,
    QUIT,
}
