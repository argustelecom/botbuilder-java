package Microsoft.Bot.Builder;

import java.util.*;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Defines the interface for a storage layer.
*/
public interface IStorage
{
	/** 
	 Reads storage items from storage.
	 
	 @param keys keys of the <see cref="IStoreItem"/> objects to read.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 the items read, indexed by key.
	 {@link DeleteAsync(string[], CancellationToken)}
	 {@link WriteAsync(IDictionary{string, object}, CancellationToken)}
	*/

	Task<java.util.Map<String, Object>> ReadAsync(String[] keys);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task<IDictionary<string, object>> ReadAsync(string[] keys, CancellationToken cancellationToken = default(CancellationToken));
	Task<Map<String, Object>> ReadAsync(String[] keys, CancellationToken cancellationToken);

	/** 
	 Writes storage items to storage.
	 
	 @param changes The items to write, indexed by key.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 {@link DeleteAsync(string[], CancellationToken)}
	 {@link ReadAsync(string[], CancellationToken)}
	*/

	Task WriteAsync(java.util.Map<String, Object> changes);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task WriteAsync(IDictionary<string, object> changes, CancellationToken cancellationToken = default(CancellationToken));
	Task WriteAsync(Map<String, Object> changes, CancellationToken cancellationToken);

	/** 
	 Deletes storage items from storage.
	 
	 @param keys keys of the <see cref="IStoreItem"/> objects to delete.
	 @param cancellationToken A cancellation token that can be used by other objects
	 or threads to receive notice of cancellation.
	 @return A task that represents the work queued to execute.
	 {@link ReadAsync(string[], CancellationToken)}
	 {@link WriteAsync(IDictionary{string, object}, CancellationToken)}
	*/

	Task DeleteAsync(String[] keys);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: Task DeleteAsync(string[] keys, CancellationToken cancellationToken = default(CancellationToken));
	Task DeleteAsync(String[] keys, CancellationToken cancellationToken);
}