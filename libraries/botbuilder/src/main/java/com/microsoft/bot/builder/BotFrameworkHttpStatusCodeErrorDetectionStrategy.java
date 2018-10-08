package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 Bot Framework HTTP Status code error detection strategy.
 
 {@link ITransientErrorDetectionStrategy }
*/
public class BotFrameworkHttpStatusCodeErrorDetectionStrategy implements ITransientErrorDetectionStrategy
{
	/** 
	 Returns true if status code in HttpRequestExceptionWithStatus exception is RequestTimeout, TooManyRequests, NotFound or greater
	 than or equal to 500 and not NotImplemented (501) or HttpVersionNotSupported (505).
	 
	 @param ex Exception to check against.
	 @return True if exception is transient otherwise false.
	*/
	public final boolean IsTransient(RuntimeException ex)
	{
		if (ex != null)
		{
			HttpRequestWithStatusException httpException;
			if ((httpException = ex instanceof HttpRequestWithStatusException ? (HttpRequestWithStatusException)ex : null) != null)
			{
				if (httpException.StatusCode == HttpStatusCode.RequestTimeout || (int)httpException.StatusCode == 429 || httpException.StatusCode == HttpStatusCode.NotFound || (httpException.StatusCode >= HttpStatusCode.InternalServerError.getValue() && httpException.StatusCode != HttpStatusCode.NotImplemented && httpException.StatusCode != HttpStatusCode.HttpVersionNotSupported))
				{
					return true;
				}
			}
		}

		return false;
	}
}