package Microsoft.Bot.Builder;

import java.util.*;

/** 
 Exposes an ETag for concurrency control.
*/
public interface IStoreItem
{
	/** 
	 Gets or sets the ETag for concurrency control.
	 
	 <value>The concurrency control ETag.</value>
	*/
	String getETag();
	void setETag(String value);
}