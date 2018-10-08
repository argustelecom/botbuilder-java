package Microsoft.Bot.Builder;

import java.util.*;

/** 
 Contains extension methods for <see cref="IStorage"/> objects.
*/
public final class StorageExtensions
{
	/** 
	 Gets and strongly types a collection of <see cref="IStoreItem"/> objects from state storage.
	 
	 <typeparam name="TStoreItem">The type of item to get from storage.</typeparam>
	 @param storage The state storage.
	 @param keys The collection of keys for the objects to get from storage.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 If the task completes successfully, the result contains a dictionary of the
	 strongly typed objects, indexed by the <paramref name="keys"/>.
	*/

	public static <TStoreItem> Task<java.util.Map<String, TStoreItem>> ReadAsync(IStorage storage, String[] keys)
	{
		return ReadAsync(storage, keys, null);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to the 'async' keyword:
//ORIGINAL LINE: public static async Task<IDictionary<string, TStoreItem>> ReadAsync<TStoreItem>(this IStorage storage, string[] keys, CancellationToken cancellationToken = default(CancellationToken)) where TStoreItem : class
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
	public static <TStoreItem> Task<Map<String, TStoreItem>> ReadAsync(IStorage storage, String[] keys, CancellationToken cancellationToken)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to 'await' in Java:
		var storeItems = await Microsoft.Bot.Builder.StorageExtensions.ReadAsync(storage, keys, cancellationToken).ConfigureAwait(false);
		HashMap<String, TStoreItem> values = new HashMap<String, TStoreItem>(keys.length);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
		for (var entry : storeItems)
		{
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (entry.Value is TStoreItem valueAsType)
			if (entry.Value instanceof TStoreItem valueAsType)
			{
				values.put(entry.Key, valueAsType);
			}
		}

		return values;
	}
}