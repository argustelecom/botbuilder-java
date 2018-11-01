package Microsoft.Bot.Builder.AI.QnA;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

/** 
 Defines options for the QnA Maker knowledge base.
*/
public class QnAMakerOptions
{
	public QnAMakerOptions()
	{
		setScoreThreshold(0.3f);
	}

	/** 
	 Gets or sets the minimum score threshold, used to filter returned results.
	 
	 Scores are normalized to the range of 0.0 to 1.0
	 before filtering.
	 <value>
	 The minimum score threshold, used to filter returned results.
	 </value>
	*/
	private float ScoreThreshold;
	public final float getScoreThreshold()
	{
		return ScoreThreshold;
	}
	public final void setScoreThreshold(float value)
	{
		ScoreThreshold = value;
	}

	/** 
	 Gets or sets the number of ranked results you want in the output.
	 
	 <value>
	 The number of ranked results you want in the output.
	 </value>
	*/
	private int Top;
	public final int getTop()
	{
		return Top;
	}
	public final void setTop(int value)
	{
		Top = value;
	}

	private Metadata[] StrictFilters;
	public final Metadata[] getStrictFilters()
	{
		return StrictFilters;
	}
	public final void setStrictFilters(Metadata[] value)
	{
		StrictFilters = value;
	}

	private Metadata[] MetadataBoost;
	public final Metadata[] getMetadataBoost()
	{
		return MetadataBoost;
	}
	public final void setMetadataBoost(Metadata[] value)
	{
		MetadataBoost = value;
	}
}