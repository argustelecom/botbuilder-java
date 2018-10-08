package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/** 
 Page of results from an enumeration.
 
 <typeparam name="T"></typeparam>
*/
public class PagedResult<T>
{
	/** 
	 Page of items.
	*/
//C# TO JAVA CONVERTER WARNING: Java does not allow direct instantiation of arrays of generic type parameters:
//ORIGINAL LINE: private T[] Items = new T[0];
	private T[] Items = (T[])new Object[0];
	public final T[] getItems()
	{
		return Items;
	}
	public final void setItems(T[] value)
	{
		Items = value;
	}

	/** 
	 Token used to page through multiple pages.
	*/
	private String ContinuationToken;
	public final String getContinuationToken()
	{
		return ContinuationToken;
	}
	public final void setContinuationToken(String value)
	{
		ContinuationToken = value;
	}
}