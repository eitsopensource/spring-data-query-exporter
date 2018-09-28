package br.com.eits.queryexport.exporter.formatters;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ar.com.fdvs.dj.domain.CustomExpression;

public class CalendarFormatter implements ObjectFormatter<Calendar>
{
	@Override
	public String format( Calendar object, String pattern )
	{
		SimpleDateFormat formatter = new SimpleDateFormat( pattern );
		return formatter.format( object.getTime() );
	}

	@Override
	public CustomExpression getCustomExpression( String propertyPath )
	{
		return null;
	}
}
