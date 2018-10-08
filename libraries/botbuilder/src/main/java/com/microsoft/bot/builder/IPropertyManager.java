package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/** 
 IPropertyManager defines implementation of a source of named properties.
*/
public interface IPropertyManager
{
	/** 
	 Create a managed state property accessor for named property.
	 
	 <typeparam name="T">type of object</typeparam>
	 @param name name of the object
	 @return property accessor for accessing the object of type T.
	*/
	<T> IStatePropertyAccessor<T> CreateProperty(String name);
}