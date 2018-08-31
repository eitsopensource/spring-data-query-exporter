package br.com.eits.queryexport.exporter.formatters;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarFormatter implements DateFormatter<Calendar>
{
	@Override
	public String format( Calendar object, String pattern )
	{
		SimpleDateFormat formatter = new SimpleDateFormat( pattern );
		return formatter.format( object.getTime() );
	}
}
