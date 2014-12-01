/*
 * Copyright (C) 2004-2014 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.group_template;

import ai.npc.AbstractNpcAI;

import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.ValidateLocation;

/**
 * Lair of Antharas AI.
 * @author St3eT, UnAfraid
 */
public final class LairOfAntharas extends AbstractNpcAI
{
	// NPC
	final private static int KNORIKS = 22857;
	final private static int DRAGON_KNIGHT = 22844;
	final private static int DRAGON_KNIGHT2 = 22845;
	final private static int ELITE_DRAGON_KNIGHT = 22846;
	
	final private static int DRAGON_GUARD = 22852;
	final private static int DRAGON_MAGE = 22853;
	// Misc
	final private static int KNIGHT_CHANCE = 30;
	final private static int KNORIKS_CHANCE = 60;
	final private static int KNORIKS_CHANCE2 = 50;
	
	private LairOfAntharas()
	{
		super(LairOfAntharas.class.getSimpleName(), "ai/group_template");
		addKillId(DRAGON_KNIGHT, DRAGON_KNIGHT2, DRAGON_GUARD, DRAGON_MAGE);
		addSpawnId(DRAGON_KNIGHT, DRAGON_KNIGHT2, DRAGON_GUARD, DRAGON_MAGE);
		addMoveFinishedId(DRAGON_GUARD, DRAGON_MAGE);
		addAggroRangeEnterId(KNORIKS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("CHECK_HOME") && (npc != null) && !npc.isDead())
		{
			if ((npc.calculateDistance(npc.getSpawn().getLocation(), false, false) > 10) && !npc.isInCombat())
			{
				((L2Attackable) npc).returnHome();
			}
			else if ((npc.getHeading() != npc.getSpawn().getHeading()) && !npc.isInCombat())
			{
				npc.setHeading(npc.getSpawn().getHeading());
				npc.broadcastPacket(new ValidateLocation(npc));
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if (npc.isScriptValue(0) && (getRandom(100) < KNORIKS_CHANCE))
		{
			if (getRandom(100) < KNORIKS_CHANCE2)
			{
				npc.setScriptValue(1);
			}
			broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.WHOS_THERE_IF_YOU_DISTURB_THE_TEMPER_OF_THE_GREAT_LAND_DRAGON_ANTHARAS_I_WILL_NEVER_FORGIVE_YOU);
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case DRAGON_KNIGHT:
			{
				if (getRandom(100) > KNIGHT_CHANCE)
				{
					final L2Npc newKnight = addSpawn(DRAGON_KNIGHT2, npc, false, 0, true);
					npc.deleteMe();
					broadcastNpcSay(newKnight, Say2.NPC_SHOUT, NpcStringId.THOSE_WHO_SET_FOOT_IN_THIS_PLACE_SHALL_NOT_LEAVE_ALIVE);
					addAttackPlayerDesire(newKnight, killer);
				}
				break;
			}
			case DRAGON_KNIGHT2:
			{
				if (getRandom(100) > KNIGHT_CHANCE)
				{
					final L2Npc eliteKnight = addSpawn(ELITE_DRAGON_KNIGHT, npc, false, 0, true);
					npc.deleteMe();
					broadcastNpcSay(eliteKnight, Say2.NPC_SHOUT, NpcStringId.IF_YOU_WISH_TO_SEE_HELL_I_WILL_GRANT_YOU_YOUR_WISH);
					addAttackPlayerDesire(eliteKnight, killer);
				}
				break;
			}
			case DRAGON_GUARD:
			case DRAGON_MAGE:
			{
				cancelQuestTimer("CHECK_HOME", npc, null);
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		final L2Attackable mob = (L2Attackable) npc;
		mob.setOnKillDelay(0);
		if ((npc.getId() == DRAGON_GUARD) || (npc.getId() == DRAGON_MAGE))
		{
			mob.setIsNoRndWalk(true);
			startQuestTimer("CHECK_HOME", 10000, npc, null, true);
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new LairOfAntharas();
	}
}