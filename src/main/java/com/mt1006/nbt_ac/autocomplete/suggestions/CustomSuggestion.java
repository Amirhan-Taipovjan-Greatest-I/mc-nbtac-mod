package com.mt1006.nbt_ac.autocomplete.suggestions;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mt1006.nbt_ac.utils.Fields;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

abstract public class CustomSuggestion
{
	abstract public String getSuggestionText();
	abstract public String getSuggestionSubtext();
	abstract public Message getSuggestionTooltip();

	public void suggest(SuggestionsBuilder suggestionsBuilder, Map<Suggestion, String> subtextMap)
	{
		String text = getSuggestionText();
		String subtext = getSuggestionSubtext();
		Message tooltip = getSuggestionTooltip();

		if (text.isEmpty() && suggestionsBuilder.getRemaining().equals(text))
		{
			if (Fields.suggestionsBuilderList == null) { return; }

			try
			{
				List<Suggestion> suggestions = (List<Suggestion>)Fields.suggestionsBuilderList.get(suggestionsBuilder);
				int start = (int)Fields.suggestionsBuilderInt.get(suggestionsBuilder);

				int len = 0;
				for (Field stringField : Fields.suggestionsBuilderStrings)
				{
					String val = (String)stringField.get(suggestionsBuilder);
					if (val != null && val.length() > len) { len = val.length(); }
				}

				suggestions.add(new Suggestion(StringRange.between(start, len), text, tooltip));
			}
			catch (Exception exception) { return; }
		}
		else
		{
			suggestionsBuilder.suggest(text, tooltip);
		}

		subtextMap.put(getLastAddedSuggestion(suggestionsBuilder), subtext);
	}

	private static Suggestion getLastAddedSuggestion(SuggestionsBuilder suggestionsBuilder)
	{
		if (Fields.suggestionsBuilderList == null) { return null; }

		try
		{
			List<Suggestion> suggestionList = (List<Suggestion>)Fields.suggestionsBuilderList.get(suggestionsBuilder);
			return suggestionList.get(suggestionList.size() - 1);
		}
		catch (Exception exception) { return null; }
	}
}
