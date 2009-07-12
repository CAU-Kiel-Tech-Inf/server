/**
 * 
 */
package sc.plugin2010.gui;

import java.util.LinkedList;
import java.util.List;

import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.plugin2010.Client;
import sc.protocol.IControllableGame;
import sc.protocol.RequestResult;
import sc.protocol.responses.PrepareGameResponse;

/**
 * @author ffi
 * 
 */
public class GamePreparation implements IGamePreparation
{
	private List<ISlot>	slots	= new LinkedList<ISlot>();
	private Observation	obs;

	public GamePreparation(Client client, int playerCount)
	{
		RequestResult<PrepareGameResponse> results = null;
		try
		{
			results = client.prepareGameAndWait(playerCount);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		PrepareGameResponse response = results.getResult();

		for (String singleResp : response.getReservations())
		{
			slots.add(new Slot(singleResp, client));
		}

		IControllableGame conGame = client.observeAndControl(response);
		obs = new Observation(conGame);
		client.setObservation(obs);
	}

	@Override
	public List<ISlot> getSlots()
	{
		return slots;
	}

	@Override
	public IObservation getObserver()
	{
		return obs;
	}
}
