package sc.server.gaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.server.network.Client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ReservationManager {

  private static Logger logger = LoggerFactory.getLogger(ReservationManager.class);
  private static Map<String, PlayerSlot> reservations = new HashMap<>();

  private ReservationManager() {
    // singleton
  }

  /**
   * If reservation code is valid register client to playerslot and start game if all clients connected
   *
   * @param client      to fill a slot
   * @param reservation code which will redeem the reservation
   *
   * @return newly filled PlayerSlot
   *
   * @throws RescuableClientException will be thrown if slot cannot be filled or reservation is unknown
   */
  public static synchronized PlayerSlot redeemReservationCode(Client client, String reservation)
      throws RescuableClientException {
    PlayerSlot result = reservations.remove(reservation);
    if (result == null)
      throw new UnknownReservationException();

    logger.info("Reservation {} was redeemed.", reservation);
    result.getRoom().fillSlot(result, client);
    return result;
  }

  /**
   * Reserve a specific slot
   *
   * @param playerSlot the slot, that is supposed to be reserved
   *
   * @return the reservation code
   *
   * @throws RuntimeException if the slot is already reserved
   */
  public synchronized static String reserve(PlayerSlot playerSlot) {
    if (reservations.containsValue(playerSlot))
      throw new RuntimeException("This slot is already reserved.");

    String key = generateUniqueId();
    reservations.put(key, playerSlot);
    return key;
  }

  /**
   * Generate a unique String ID.
   *
   * @return the unique ID
   */
  private synchronized static String generateUniqueId() {
    String key;
    do {
      key = UUID.randomUUID().toString();
    } while (reservations.containsKey(key));
    return key;
  }

  /**
   * Remove reservation with given redeem code and free that slot.
   *
   * @param reservation the redeem code
   */
  public static synchronized void freeReservation(String reservation) {
    PlayerSlot slot = reservations.remove(reservation);
    slot.free();
  }

}
