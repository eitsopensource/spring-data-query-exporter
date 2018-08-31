package br.com.eits.queryexport;

import lombok.Getter;

@Getter
public class QueryExportConfiguration
{
	private final String reportTitle;
	private final String reportImage;

	public QueryExportConfiguration( String reportTitle, String reportImage )
	{
		this.reportTitle = reportTitle;
		this.reportImage = reportImage;
	}
}
