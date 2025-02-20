package com.mt1006.nbt_ac.mixin.suggestions.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mt1006.nbt_ac.autocomplete.NbtSuggestionManager;
import com.mt1006.nbt_ac.utils.MixinUtils;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.CompletableFuture;

@Mixin(NbtPathArgument.class)
abstract public class NbtPathArgumentMixin implements ArgumentType<CompoundTag>
{
	@Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder)
	{
		try
		{
			String name = getResourceName(commandContext);
			if (name == null) { return Suggestions.empty(); }

			String tag = suggestionsBuilder.getRemaining();

			return NbtSuggestionManager.loadFromName(name, tag, suggestionsBuilder, true);
		}
		catch (Exception exception)
		{
			return Suggestions.empty();
		}
	}

	private String getResourceName(CommandContext<?> commandContext)
	{
		String commandName = MixinUtils.getCommandName(commandContext);
		if (commandName.equals("data")) { return getResourceNameForDataCommand(commandContext); }
		else if (commandContext.getChild() != null) { return getResourceName(commandContext.getChild()); }
		return null;
	}

	private String getResourceNameForDataCommand(CommandContext<?> commandContext)
	{
		String blockArgument = "targetPos";
		String entityArgument = "target";
		String instruction = MixinUtils.getNodeString(commandContext, 1);
		String type = MixinUtils.getNodeString(commandContext, 2);

		switch (instruction)
		{
			case "get":
			case "remove":
				break;

			case "modify":
				if (commandContext.getNodes().size() > 7)
				{
					String modification = MixinUtils.getNodeString(commandContext, 5);

					if (modification.equals("insert")) { type = MixinUtils.getNodeString(commandContext, 8); }
					else { type = MixinUtils.getNodeString(commandContext, 7); }

					blockArgument = "sourcePos";
					entityArgument = "source";
				}
				break;

			default:
				return null;
		}

		switch (type)
		{
			case "block":
				Coordinates coords = commandContext.getArgument(blockArgument, Coordinates.class);
				return MixinUtils.blockFromCoords(coords);

			case "entity":
				EntitySelector entitySelector = commandContext.getArgument(entityArgument, EntitySelector.class);
				return MixinUtils.entityFromEntitySelector(entitySelector);
		}

		return null;
	}
}
