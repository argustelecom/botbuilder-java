package com.microsoft.bot.builder;

import java.util.*;

/** 
 Exposes an ETag for concurrency control.
*/
public interface StoreItem
{
	/** 
	 Gets or sets the ETag for concurrency control.
	 
	 <value>The concurrency control ETag.</value>
	*/
	String getETag();
	void setETag(String value);
}