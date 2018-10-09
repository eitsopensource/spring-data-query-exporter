package br.com.eits.queryexport;

import lombok.Data;

@Data(staticConstructor = "of")
public class QueryColumn
{
	private final String attributePath;
	private final String localizedLabel;
	private final int percentualWidth;
	private final String dateTimePattern;
	private final boolean formattingEnabled;
}
